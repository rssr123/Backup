DROP PROCEDURE sp_getrefundinfo;
CREATE OR REPLACE PROCEDURE sp_getrefundinfo(
    i_orn_no NVARCHAR(255),
    i_rtt_app_no NVARCHAR(255)
)
    RETURNING 
        NVARCHAR(255) AS app_no, 
        NVARCHAR(10) AS app_status, 
        NVARCHAR(255) AS app_msg, 
        NVARCHAR(50) AS app_rejected_reason,
        NVARCHAR(255) AS slip_no,
        BLOB AS file;

    DEFINE v_rtt_app_no NVARCHAR(255);
    DEFINE v_rtt_status NVARCHAR(10);
    DEFINE v_msg NVARCHAR(255);
    DEFINE v_param_grp_nm NVARCHAR(50); -- reject reason
    DEFINE v_refund_slip_no NVARCHAR(255);
    DEFINE v_file_content BLOB;
    DEFINE v_reject_reason NVARCHAR(255);
    DEFINE v_rtt_wf_id INT;
    DEFINE v_rtt_wf_hist_id INT;

-- FIND the Refund Status

SELECT rtt_wf_id, rtt_status 
INTO v_rtt_wf_id, v_rtt_status
FROM rms_rtt_wf WHERE rtt_app_no = i_rtt_app_no AND orn_no = i_orn_no;

IF (v_rtt_status = 'RR') THEN 
    SELECT rtt_wf_hist_id INTO v_rtt_wf_hist_id from rms_rtt_wf_hist where rtt_status = 'RR' and rtt_wf_id = v_rtt_wf_id;
    SELECT msg  into v_reject_reason from rms_rtt_wf_msg where rtt_wf_hist_id = v_rtt_wf_hist_id and rtt_wf_id = v_rtt_wf_id;
ELSE
    LET v_reject_reason = NULL;  -- or '' if you prefer

END IF;

    SELECT 
        rtt.rtt_app_no,
        rtt.rtt_status, 
        msg.msg, 
        rtt.refund_slip_no,
        doc.file_content
    INTO 
        v_rtt_app_no,
        v_rtt_status, 
        v_msg, 
        v_refund_slip_no,
        v_file_content
    FROM 
        rms_rtt_wf rtt
    LEFT JOIN 
        rms_rtt_doc doc ON rtt.rtt_wf_id = doc.rtt_wf_id
    LEFT JOIN rms_rtt_wf_msg msg
       ON msg.rtt_wf_id = rtt.rtt_wf_id
      AND msg.dt_created = (
            SELECT MAX(m2.dt_created)
            FROM rms_rtt_wf_msg m2
            WHERE m2.rtt_wf_id = rtt.rtt_wf_id
      )
    WHERE 
        rtt.rtt_app_no = i_rtt_app_no 
    AND
        rtt.orn_no = i_orn_no
    LIMIT 1;  

    RETURN 
        v_rtt_app_no,
        v_rtt_status, 
        v_msg, 
        v_reject_reason,
        v_refund_slip_no,
        v_file_content;
END PROCEDURE;
GO

DROP PROCEDURE sp_getrefundpttlisting;
CREATE OR REPLACE PROCEDURE sp_getrefundpttlisting(
    i_page INT, 
    i_size INT,
    i_orn_no NVARCHAR(20),
    i_orn_dt_fr DATE, 
    i_orn_dt_to DATE, 
    i_ent_nm LVARCHAR(400), 
    i_txn_id NVARCHAR(255),
    i_order_status NVARCHAR(50),
    i_rcpt_no NVARCHAR(20)
)
RETURNING NVARCHAR(20) AS orn_no,
          DATETIME YEAR TO SECOND AS orn_dt,
          NVARCHAR(255) AS txn_id,
          DECIMAL(16,2) AS total_amt,
          NVARCHAR(50) AS order_status,
          NVARCHAR(20) AS rcpt_no,
          INT AS total,
          INT AS mtt_id,
          NVARCHAR(20) AS rms_type,
          NVARCHAR(30) AS rtt_status;

-- Variable declarations
DEFINE o_mtt_id INT;
DEFINE o_ss_cd NVARCHAR(10);
DEFINE o_orn_no NVARCHAR(20);
DEFINE o_txn_id NVARCHAR(255);
DEFINE o_total_amt DECIMAL(16,2);
DEFINE o_orn_dt DATETIME YEAR TO SECOND;
DEFINE o_order_status NVARCHAR(50);
DEFINE o_rcpt_no NVARCHAR(20);
DEFINE calculatedpage INT;
DEFINE total INT;
DEFINE o_rms_type NVARCHAR(20);
DEFINE o_rtt_status NVARCHAR(25);

-- New optimization variables
DEFINE effective_orn_dt_fr DATE;
DEFINE effective_orn_dt_to DATE;
DEFINE has_any_filter INT;
DEFINE has_rcpt_filter INT;
DEFINE has_txn_filter INT;
DEFINE query_scenario INT; -- 1: rcpt_no only, 2: txn_id only, 3: orn_no passed, 4: default

-- Input validation
IF (i_page IS NULL OR i_page < 1) OR (i_size IS NULL OR i_size < 1) THEN
    RAISE EXCEPTION -746, 0, "i_page or i_size is null or less than 1";
END IF;

LET calculatedpage = (i_page - 1) * i_size;

-- Determine query scenario for optimization
IF i_rcpt_no IS NOT NULL AND i_orn_no IS NULL AND i_txn_id IS NULL AND 
   i_orn_dt_fr IS NULL AND i_orn_dt_to IS NULL THEN
    LET query_scenario = 1; -- Receipt number only scenario
ELIF i_txn_id IS NOT NULL AND i_orn_no IS NULL AND i_rcpt_no IS NULL AND 
     i_orn_dt_fr IS NULL AND i_orn_dt_to IS NULL THEN
    LET query_scenario = 2; -- Transaction ID only scenario
ELIF i_orn_no IS NOT NULL THEN
    LET query_scenario = 3; -- Order number scenario (prioritized)
ELSE
    LET query_scenario = 4; -- Default scenario
END IF;

-- Set filter flags
IF i_orn_no IS NOT NULL OR i_orn_dt_fr IS NOT NULL OR i_orn_dt_to IS NOT NULL OR
   i_txn_id IS NOT NULL OR i_rcpt_no IS NOT NULL THEN
    LET has_any_filter = 1;
ELSE
    LET has_any_filter = 0;
END IF;

IF i_rcpt_no IS NOT NULL THEN
    LET has_rcpt_filter = 1;
ELSE
    LET has_rcpt_filter = 0;
END IF;

IF i_txn_id IS NOT NULL THEN
    LET has_txn_filter = 1;
ELSE
    LET has_txn_filter = 0;
END IF;

-- Set effective date ranges (similar to sp_getmttlisting)
-- Default to 2-day window when no filters are provided
LET effective_orn_dt_fr = NVL(i_orn_dt_fr, 
    CASE WHEN has_any_filter = 0 THEN TODAY - 1 UNITS DAY ELSE NULL END);
LET effective_orn_dt_to = NVL(i_orn_dt_to, 
    CASE WHEN has_any_filter = 0 THEN TODAY + 1 UNITS DAY ELSE NULL END);

-- Execute based on scenario
IF query_scenario = 1 THEN
    -- Scenario 1: Receipt number only - Start from receipt tables for better performance
    FOREACH c_getmtt FOR
        SELECT SKIP calculatedpage FIRST i_size
               orn_no, orn_dt, txn_id, total_amt, order_status, rcpt_no, 
               COUNT(*) OVER() as total_count, mtt_id, rms_type, rtt_status
        INTO o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
             o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status
        FROM (
            SELECT t1.orn_no,
                   t1.orn_dt,
                   COALESCE(pg.pg_txn_id, '') as txn_id,
                   t1.total_amt,
                   COALESCE(p.nm_en, t1.order_status) as order_status,
                   receipt_data.rcpt_no,
                   t1.mtt_id,
                   t1.rms_type,
                   COALESCE(( SELECT r1.rtt_status FROM rms_rtt_wf r1 WHERE r1.orn_no = t1.orn_no AND r1.dt_created = (
                            SELECT MAX(r2.dt_created)
                            FROM rms_rtt_wf r2
                            WHERE r2.orn_no = t1.orn_no)), '') AS rtt_status
            FROM (
                -- Get receipt data first when filtering by receipt only
                SELECT mtt_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY rcpt_dt) as row_num
                FROM (
                    SELECT mtt_id, rcpt_no, rcpt_dt,
                           MAX(CASE WHEN source_type = 1 THEN 1 ELSE 0 END) 
                           OVER (PARTITION BY mtt_id) as has_direct_receipts,
                           source_type
                    FROM (
                        -- Direct MTT receipts
                        SELECT mtt_id, rcpt_no, rcpt_dt, 1 as source_type
                        FROM rms_mtt_rcpt
                        WHERE rcpt_no LIKE '%' || i_rcpt_no || '%'
                        UNION ALL
                        -- OTC receipts
                        SELECT o.mtt_id, rorc.rcpt_no, rorc.rcpt_dt, 2 as source_type
                        FROM rms_otc o
                        JOIN rms_otc_rcpt rorc ON o.otc_id = rorc.otc_id
                        WHERE rorc.rcpt_no LIKE '%' || i_rcpt_no || '%'
                    ) all_receipts
                ) receipts_with_flags
                WHERE (source_type = 1) OR (source_type = 2 AND has_direct_receipts = 0)
            ) receipt_data
            JOIN rms_mtt t1 ON receipt_data.mtt_id = t1.mtt_id 
                AND receipt_data.row_num = 1 
                AND t1.order_status = 'P'
            LEFT JOIN rms_param p ON t1.order_status = p.param_cd AND p.param_grp_nm = 'OrderStatus'
            LEFT JOIN (
                SELECT mtt_id, pg_txn_id,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY dt_created DESC) as rn
                FROM rms_mtt_pg 
                WHERE pg_txn_status = 0
            ) pg ON t1.mtt_id = pg.mtt_id AND pg.rn = 1
            ORDER BY t1.orn_dt DESC
        ) filtered_data
        RETURN o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
               o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status WITH RESUME;
    END FOREACH;

ELIF query_scenario = 2 THEN
    -- Scenario 2: Transaction ID only - Start from payment table
    FOREACH c_getmtt FOR
        SELECT SKIP calculatedpage FIRST i_size
               orn_no, orn_dt, txn_id, total_amt, order_status, rcpt_no, 
               COUNT(*) OVER() as total_count, mtt_id, rms_type, rtt_status
        INTO o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
             o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status
        FROM (
            SELECT t1.orn_no,
                   t1.orn_dt,
                   pg_data.pg_txn_id as txn_id,
                   t1.total_amt,
                   COALESCE(p.nm_en, t1.order_status) as order_status,
                   COALESCE(mr.rcpt_no, orc.rcpt_no, '') as rcpt_no,
                   t1.mtt_id,
                   t1.rms_type,
                   COALESCE(( SELECT r1.rtt_status FROM rms_rtt_wf r1 WHERE r1.orn_no = t1.orn_no AND r1.dt_created = (
                            SELECT MAX(r2.dt_created)
                            FROM rms_rtt_wf r2
                            WHERE r2.orn_no = t1.orn_no)), '') AS rtt_status
            FROM (
                -- Start from payment table when filtering by txn_id
                SELECT mtt_id, pg_txn_id,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY dt_created DESC) as rn
                FROM rms_mtt_pg 
                WHERE pg_txn_status = 0
                  AND pg_txn_id LIKE '%' || i_txn_id || '%'
            ) pg_data
            JOIN rms_mtt t1 ON pg_data.mtt_id = t1.mtt_id 
                AND pg_data.rn = 1 
                AND t1.order_status = 'P'
            LEFT JOIN rms_param p ON t1.order_status = p.param_cd AND p.param_grp_nm = 'OrderStatus'
            LEFT JOIN (
                SELECT mtt_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY rcpt_dt) as rn
                FROM rms_mtt_rcpt
            ) mr ON t1.mtt_id = mr.mtt_id AND mr.rn = 1
            LEFT JOIN rms_otc o ON t1.mtt_id = o.mtt_id AND mr.mtt_id IS NULL
            LEFT JOIN (
                SELECT otc_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY otc_id ORDER BY rcpt_dt) as rn
                FROM rms_otc_rcpt
            ) orc ON o.otc_id = orc.otc_id AND orc.rn = 1
            ORDER BY t1.orn_dt DESC
        ) filtered_data
        RETURN o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
               o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status WITH RESUME;
    END FOREACH;

ELIF query_scenario = 3 THEN
    -- Scenario 3: Order number passed - Filter by order number first
    FOREACH c_getmtt FOR
        SELECT SKIP calculatedpage FIRST i_size
               orn_no, orn_dt, txn_id, total_amt, order_status, rcpt_no, 
               COUNT(*) OVER() as total_count, mtt_id, rms_type, rtt_status
        INTO o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
             o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status
        FROM (
            SELECT t1.orn_no,
                   t1.orn_dt,
                   COALESCE(pg.pg_txn_id, '') as txn_id,
                   t1.total_amt,
                   COALESCE(p.nm_en, t1.order_status) as order_status,
                   COALESCE(mr.rcpt_no, orc.rcpt_no, '') as rcpt_no,
                   t1.mtt_id,
                   t1.rms_type,
                   COALESCE(( SELECT r1.rtt_status FROM rms_rtt_wf r1 WHERE r1.orn_no = t1.orn_no AND r1.dt_created = (
                            SELECT MAX(r2.dt_created)
                            FROM rms_rtt_wf r2
                            WHERE r2.orn_no = t1.orn_no)), '') AS rtt_status
            FROM rms_mtt t1
            LEFT JOIN rms_param p ON t1.order_status = p.param_cd AND p.param_grp_nm = 'OrderStatus'
            LEFT JOIN (
                SELECT mtt_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY rcpt_dt) as rn
                FROM rms_mtt_rcpt
            ) mr ON t1.mtt_id = mr.mtt_id AND mr.rn = 1
            LEFT JOIN rms_otc o ON t1.mtt_id = o.mtt_id AND mr.mtt_id IS NULL
            LEFT JOIN (
                SELECT otc_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY otc_id ORDER BY rcpt_dt) as rn
                FROM rms_otc_rcpt
            ) orc ON o.otc_id = orc.otc_id AND orc.rn = 1
            LEFT JOIN (
                SELECT mtt_id, pg_txn_id,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY dt_created DESC) as rn
                FROM rms_mtt_pg 
                WHERE pg_txn_status = 0
            ) pg ON t1.mtt_id = pg.mtt_id AND pg.rn = 1
            WHERE t1.order_status = 'P'
              AND t1.orn_no LIKE '%' || i_orn_no || '%'
              AND (i_rcpt_no IS NULL OR COALESCE(mr.rcpt_no, orc.rcpt_no) LIKE '%' || i_rcpt_no || '%')
              AND (i_txn_id IS NULL OR pg.pg_txn_id LIKE '%' || i_txn_id || '%')
            ORDER BY t1.orn_dt DESC
        ) filtered_data
        RETURN o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
               o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status WITH RESUME;
    END FOREACH;

ELSE
    -- Scenario 4: Default with effective date optimization
    FOREACH c_getmtt FOR
        SELECT SKIP calculatedpage FIRST i_size
               orn_no, orn_dt, txn_id, total_amt, order_status, rcpt_no, 
               COUNT(*) OVER() as total_count, mtt_id, rms_type, rtt_status
        INTO o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
             o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status
        FROM (
            SELECT t1.orn_no,
                   t1.orn_dt,
                   COALESCE(pg.pg_txn_id, '') as txn_id,
                   t1.total_amt,
                   COALESCE(p.nm_en, t1.order_status) as order_status,
                   COALESCE(mr.rcpt_no, orc.rcpt_no, '') as rcpt_no,
                   t1.mtt_id,
                   t1.rms_type,
                   COALESCE(( SELECT r1.rtt_status FROM rms_rtt_wf r1 WHERE r1.orn_no = t1.orn_no AND r1.dt_created = (
                            SELECT MAX(r2.dt_created)
                            FROM rms_rtt_wf r2
                            WHERE r2.orn_no = t1.orn_no)), '') AS rtt_status
            FROM rms_mtt t1
            LEFT JOIN rms_param p ON t1.order_status = p.param_cd AND p.param_grp_nm = 'OrderStatus'
            LEFT JOIN (
                SELECT mtt_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY mtt_id ORDER BY rcpt_dt) as rn
                FROM rms_mtt_rcpt
                WHERE (i_rcpt_no IS NULL OR rcpt_no LIKE '%' || i_rcpt_no || '%')
            ) mr ON t1.mtt_id = mr.mtt_id AND mr.rn = 1
            LEFT JOIN rms_otc o ON t1.mtt_id = o.mtt_id AND mr.mtt_id IS NULL
            LEFT JOIN (
                SELECT otc_id, rcpt_no,
                       ROW_NUMBER() OVER (PARTITION BY otc_id ORDER BY rcpt_dt) as rn
                FROM rms_otc_rcpt
                WHERE (i_rcpt_no IS NULL OR rcpt_no LIKE '%' || i_rcpt_no || '%')
            ) orc ON o.otc_id = orc.otc_id AND orc.rn = 1
            LEFT JOIN (
                SELECT pg.mtt_id, pg.pg_txn_id,
                       ROW_NUMBER() OVER (PARTITION BY pg.mtt_id ORDER BY pg.dt_created DESC) as rn
                FROM rms_mtt_pg pg
                JOIN rms_mtt m ON pg.mtt_id = m.mtt_id
                WHERE 
					  (effective_orn_dt_fr IS NULL OR CAST(m.orn_dt AS DATE) >= effective_orn_dt_fr)
                  AND (effective_orn_dt_to IS NULL OR CAST(m.orn_dt AS DATE) <= effective_orn_dt_to)
                  AND pg.pg_txn_status = 0
            ) pg ON t1.mtt_id = pg.mtt_id AND pg.rn = 1
            WHERE (effective_orn_dt_fr IS NULL OR CAST(t1.orn_dt AS DATE) >= effective_orn_dt_fr)
              AND (effective_orn_dt_to IS NULL OR CAST(t1.orn_dt AS DATE) <= effective_orn_dt_to)			
              AND t1.order_status = 'P'
              AND (i_orn_no IS NULL OR t1.orn_no LIKE '%' || i_orn_no || '%')
              AND (has_rcpt_filter = 0 OR mr.mtt_id IS NOT NULL OR orc.otc_id IS NOT NULL)
              AND (has_txn_filter = 0 OR pg.mtt_id IS NOT NULL)
            ORDER BY t1.orn_dt DESC
        ) filtered_data
        RETURN o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_order_status, 
               o_rcpt_no, total, o_mtt_id, o_rms_type, o_rtt_status WITH RESUME;
    END FOREACH;
END IF;

END PROCEDURE;
GO

DROP PROCEDURE sp_getrefundtht;
CREATE OR REPLACE PROCEDURE sp_getrefundtht(
    i_page INT, 
    i_size INT,
    i_orn_no NVARCHAR(20),
    i_rcpt_no NVARCHAR(20), 
    i_txn_id NVARCHAR(255),
    i_refund_slip_no NVARCHAR(20),
    i_created_by NVARCHAR(255),
    i_order_status NVARCHAR(50),
    i_rtt_app_no NVARCHAR(255),
    i_rms_type NVARCHAR(10),
    i_orn_dt_fr DATE, 
    i_orn_dt_to DATE
)
RETURNING NVARCHAR(20) AS orn_no,
          NVARCHAR(20) AS refund_slip_no,
          DATETIME YEAR TO SECOND AS orn_dt,
          NVARCHAR(255) AS txn_id,
          DECIMAL(16,2) AS total_amt,
          NVARCHAR(20) AS rms_type,
          NVARCHAR(50) AS order_status,
          NVARCHAR(20) AS rcpt_no,
          NVARCHAR(255) AS rtt_app_no,
          DATETIME YEAR TO SECOND AS date_expiry,
          NVARCHAR(25) AS rtt_status,
          INT AS total,
          INT AS mtt_id;

-- Variable declarations
DEFINE o_mtt_id INT;
DEFINE o_ss_cd NVARCHAR(10);
DEFINE o_orn_no NVARCHAR(20);
DEFINE o_refund_slip_no NVARCHAR(20);
DEFINE o_orn_dt DATETIME YEAR TO SECOND;
DEFINE o_txn_id NVARCHAR(255);
DEFINE o_total_amt DECIMAL(16,2);
DEFINE o_rms_type NVARCHAR(20);
DEFINE o_order_status NVARCHAR(50);
DEFINE o_rcpt_no NVARCHAR(20);
DEFINE o_rtt_app_no NVARCHAR(255);
DEFINE calculatedpage INT;
DEFINE total INT;
DEFINE o_date_expiry DATETIME YEAR TO SECOND;
DEFINE o_rtt_status NVARCHAR(25);

-- New date optimization variables
DEFINE effective_orn_dt_fr DATE;
DEFINE effective_orn_dt_to DATE;
DEFINE has_any_filter INT;

-- Input validation
IF (i_page IS NULL OR i_page < 1) OR (i_size IS NULL OR i_size < 1) THEN
    RAISE EXCEPTION -746, 0, "i_page or i_size is null or less than 1";
END IF;

LET calculatedpage = (i_page - 1) * i_size;

-- Set filter flags and date logic based on RMS type
IF i_rms_type = 'Online' THEN
    -- For Online RMS type, check if any search criteria provided (excluding i_rms_type itself)
    IF i_orn_no IS NOT NULL OR i_orn_dt_fr IS NOT NULL OR i_orn_dt_to IS NOT NULL OR
       i_txn_id IS NOT NULL OR i_rcpt_no IS NOT NULL OR i_refund_slip_no IS NOT NULL OR
       i_rtt_app_no IS NOT NULL OR i_created_by IS NOT NULL THEN
        LET has_any_filter = 1;
    ELSE
        LET has_any_filter = 0;
    END IF;
    
    -- Set effective date ranges with 2-day default when no filters provided for Online
    LET effective_orn_dt_fr = NVL(i_orn_dt_fr, 
        CASE WHEN has_any_filter = 0 THEN TODAY - 1 UNITS DAY ELSE NULL END);
    LET effective_orn_dt_to = NVL(i_orn_dt_to, 
        CASE WHEN has_any_filter = 0 THEN TODAY + 1 UNITS DAY ELSE NULL END);
ELSE
    -- For non-Online RMS types, only use explicitly provided dates
    LET has_any_filter = 1; -- Always treat as filtered to avoid default date logic
    LET effective_orn_dt_fr = i_orn_dt_fr;
    LET effective_orn_dt_to = i_orn_dt_to;
END IF;

-- Optimized total count logic with date filtering
LET total = 
    CASE 
        WHEN i_refund_slip_no IS NOT NULL AND i_refund_slip_no != '' THEN (
            SELECT COUNT(*) FROM rms_rtt WHERE refund_slip_no = i_refund_slip_no
        )
        WHEN i_rtt_app_no IS NOT NULL AND i_rtt_app_no != '' THEN (
            SELECT COUNT(*) FROM rms_rtt_wf WHERE rtt_app_no = i_rtt_app_no
        )
        ELSE (
            SELECT COUNT(DISTINCT t1.mtt_id)
            FROM rms_mtt t1
            LEFT JOIN (
                -- Get first MTT receipt (for online payments)
                SELECT mtt_id, rcpt_no
                FROM rms_mtt_rcpt mr1
                WHERE rcpt_dt = (
                    SELECT MIN(rcpt_dt) 
                    FROM rms_mtt_rcpt mr2 
                    WHERE mr2.mtt_id = mr1.mtt_id
                )
                AND NOT EXISTS (SELECT 1 FROM rms_otc WHERE mtt_id = mr1.mtt_id)
            ) online_rcpt ON t1.mtt_id = online_rcpt.mtt_id
            LEFT JOIN (
                -- Get first OTC receipt (for OTC payments)
                SELECT o.mtt_id, orc.rcpt_no
                FROM rms_otc o
                JOIN rms_otc_rcpt orc ON o.otc_id = orc.otc_id
                WHERE orc.rcpt_dt = (
                    SELECT MIN(orc2.rcpt_dt)
                    FROM rms_otc_rcpt orc2
                    WHERE orc2.otc_id = o.otc_id
                )
            ) otc_rcpt ON t1.mtt_id = otc_rcpt.mtt_id
            LEFT JOIN (
                SELECT mtt_id, pg_txn_id
                FROM rms_mtt_pg pg1
                WHERE pg_txn_status = 0
                  AND dt_created = (
                      SELECT MAX(dt_created)
                      FROM rms_mtt_pg pg2
                      WHERE pg2.mtt_id = pg1.mtt_id AND pg2.pg_txn_status = 0
                  )
            ) t5 ON t1.mtt_id = t5.mtt_id
            WHERE (effective_orn_dt_fr IS NULL OR CAST(t1.orn_dt AS DATE) >= effective_orn_dt_fr)
              AND (effective_orn_dt_to IS NULL OR CAST(t1.orn_dt AS DATE) <= effective_orn_dt_to)
              AND (i_orn_no IS NULL OR t1.orn_no LIKE '%' || i_orn_no || '%')
              AND (i_rcpt_no IS NULL OR COALESCE(online_rcpt.rcpt_no, otc_rcpt.rcpt_no) LIKE '%' || i_rcpt_no || '%')
              AND (i_txn_id IS NULL OR t5.pg_txn_id LIKE '%' || i_txn_id || '%')
              AND (i_created_by IS NULL OR t1.created_by LIKE '%' || i_created_by || '%')
              AND (i_order_status IS NULL OR t1.order_status = i_order_status)
              AND (i_rms_type IS NULL OR t1.rms_type = i_rms_type)
        )
    END;

-- Main query logic - split into two optimized paths
IF (i_refund_slip_no IS NULL OR i_refund_slip_no = '') AND (i_rtt_app_no IS NULL OR i_rtt_app_no = '') THEN

    -- Path 1: General search without specific refund/app filters
    FOREACH c_getTHT FOR
        SELECT SKIP calculatedpage LIMIT i_size
               t1.orn_no,
               t1.orn_dt,
               COALESCE(t5.pg_txn_id, '') as pg_txn_id,
               t1.total_amt,
               t1.rms_type,
               COALESCE(t3.nm_en, t1.order_status) as order_status,
               COALESCE(online_rcpt.rcpt_no, otc_rcpt.rcpt_no, '') as rcpt_no,
               t1.mtt_id
        INTO o_orn_no, o_orn_dt, o_txn_id, o_total_amt, o_rms_type, 
             o_order_status, o_rcpt_no, o_mtt_id
        FROM rms_mtt t1
        -- Optimized receipt join - separate online and OTC paths
        LEFT JOIN (
            -- Get first MTT receipt (for online payments)
            SELECT mtt_id, rcpt_no
            FROM rms_mtt_rcpt mr1
            WHERE rcpt_dt = (
                SELECT MIN(rcpt_dt) 
                FROM rms_mtt_rcpt mr2 
                WHERE mr2.mtt_id = mr1.mtt_id
            )
            AND NOT EXISTS (SELECT 1 FROM rms_otc WHERE mtt_id = mr1.mtt_id)
        ) online_rcpt ON t1.mtt_id = online_rcpt.mtt_id
        LEFT JOIN (
            -- Get first OTC receipt (for OTC payments)
            SELECT o.mtt_id, orc.rcpt_no
            FROM rms_otc o
            JOIN rms_otc_rcpt orc ON o.otc_id = orc.otc_id
            WHERE orc.rcpt_dt = (
                SELECT MIN(orc2.rcpt_dt)
                FROM rms_otc_rcpt orc2
                WHERE orc2.otc_id = o.otc_id
            )
        ) otc_rcpt ON t1.mtt_id = otc_rcpt.mtt_id
        -- Order status lookup
        LEFT JOIN rms_param t3 ON t1.order_status = t3.param_cd AND t3.param_grp_nm = 'OrderStatus'
        -- Payment info - get latest successful payment
        LEFT JOIN (
            SELECT mtt_id, pg_txn_id
            FROM rms_mtt_pg pg1
            WHERE pg_txn_status = 0
              AND dt_created = (
                  SELECT MAX(dt_created)
                  FROM rms_mtt_pg pg2
                  WHERE pg2.mtt_id = pg1.mtt_id AND pg2.pg_txn_status = 0
              )
        ) t5 ON t1.mtt_id = t5.mtt_id
        WHERE (effective_orn_dt_fr IS NULL OR CAST(t1.orn_dt AS DATE) >= effective_orn_dt_fr)
          AND (effective_orn_dt_to IS NULL OR CAST(t1.orn_dt AS DATE) <= effective_orn_dt_to)
          AND (i_orn_no IS NULL OR t1.orn_no LIKE '%' || i_orn_no || '%')
          AND (i_rcpt_no IS NULL OR COALESCE(online_rcpt.rcpt_no, otc_rcpt.rcpt_no) LIKE '%' || i_rcpt_no || '%')
          AND (i_txn_id IS NULL OR t5.pg_txn_id LIKE '%' || i_txn_id || '%')
          AND (i_created_by IS NULL OR t1.created_by LIKE '%' || i_created_by || '%')
          AND (i_order_status IS NULL OR t1.order_status = i_order_status)
          AND (i_rms_type IS NULL OR t1.rms_type = i_rms_type)
        ORDER BY t1.orn_dt DESC

        -- Get RTT related data efficiently using single queries
        LET o_rtt_app_no = (
            SELECT COALESCE(rtt_app_no, 'N/A')
            FROM rms_rtt_wf
            WHERE orn_no = o_orn_no
              AND dt_created = (SELECT MAX(dt_created) FROM rms_rtt_wf WHERE orn_no = o_orn_no)
        );

        LET o_refund_slip_no = (
            SELECT COALESCE(refund_slip_no, 'N/A')
            FROM rms_rtt
            WHERE orn_no = o_orn_no
              AND dt_created = (SELECT MAX(dt_created) FROM rms_rtt WHERE orn_no = o_orn_no)
        );

        LET o_date_expiry = (
            SELECT date_expiry
            FROM rms_rtt
            WHERE refund_slip_no = o_refund_slip_no
              AND dt_created = (SELECT MAX(dt_created) FROM rms_rtt WHERE refund_slip_no = o_refund_slip_no)
        );

        LET o_rtt_status = (
            SELECT COALESCE(rtt_status, 'N/A')
            FROM rms_rtt_wf
            WHERE orn_no = o_orn_no
              AND dt_created = (SELECT MAX(dt_created) FROM rms_rtt_wf WHERE orn_no = o_orn_no)
        );

        RETURN o_orn_no, o_refund_slip_no, o_orn_dt, o_txn_id, o_total_amt, 
               o_rms_type, o_order_status, o_rcpt_no, o_rtt_app_no, o_date_expiry, 
               o_rtt_status, total, o_mtt_id WITH RESUME;

    END FOREACH;

ELSE 

    -- Path 2: Search with specific refund slip or app number filters
    FOREACH c_getTHT FOR
        SELECT SKIP calculatedpage LIMIT i_size
               t1.orn_no,
               t1.orn_dt,
               t1.total_amt,
               COALESCE(t3.nm_en, t1.order_status) as order_status,
               COALESCE(online_rcpt.rcpt_no, otc_rcpt.rcpt_no, '') as rcpt_no,
               t1.mtt_id,
               COALESCE(t5.pg_txn_id, '') as pg_txn_id,
               t1.rms_type,
               COALESCE(t6.refund_slip_no, 'N/A') as refund_slip_no,
               COALESCE(t6.rtt_app_no, 'N/A') as rtt_app_no,
               COALESCE(t6.rtt_status, 'N/A') as rtt_status
        INTO o_orn_no, o_orn_dt, o_total_amt, o_order_status, o_rcpt_no, 
             o_mtt_id, o_txn_id, o_rms_type, o_refund_slip_no, o_rtt_app_no, o_rtt_status
        FROM rms_mtt t1
        -- Optimized receipt join - separate online and OTC paths
        LEFT JOIN (
            -- Get first MTT receipt (for online payments)
            SELECT mtt_id, rcpt_no
            FROM rms_mtt_rcpt mr1
            WHERE rcpt_dt = (
                SELECT MIN(rcpt_dt) 
                FROM rms_mtt_rcpt mr2 
                WHERE mr2.mtt_id = mr1.mtt_id
            )
            AND NOT EXISTS (SELECT 1 FROM rms_otc WHERE mtt_id = mr1.mtt_id)
        ) online_rcpt ON t1.mtt_id = online_rcpt.mtt_id
        LEFT JOIN (
            -- Get first OTC receipt (for OTC payments)
            SELECT o.mtt_id, orc.rcpt_no
            FROM rms_otc o
            JOIN rms_otc_rcpt orc ON o.otc_id = orc.otc_id
            WHERE orc.rcpt_dt = (
                SELECT MIN(orc2.rcpt_dt)
                FROM rms_otc_rcpt orc2
                WHERE orc2.otc_id = o.otc_id
            )
        ) otc_rcpt ON t1.mtt_id = otc_rcpt.mtt_id
        -- Order status lookup
        LEFT JOIN rms_param t3 ON t1.order_status = t3.param_cd AND t3.param_grp_nm = 'OrderStatus'
        -- Payment info
        LEFT JOIN (
            SELECT mtt_id, pg_txn_id
            FROM rms_mtt_pg pg1
            WHERE pg_txn_status = 0
              AND dt_created = (
                  SELECT MAX(dt_created)
                  FROM rms_mtt_pg pg2
                  WHERE pg2.mtt_id = pg1.mtt_id AND pg2.pg_txn_status = 0
              )
        ) t5 ON t1.mtt_id = t5.mtt_id
        -- RTT workflow info - get latest per orn_no
        LEFT JOIN (
            SELECT orn_no, refund_slip_no, rtt_app_no, rtt_status,
                   ROW_NUMBER() OVER (PARTITION BY orn_no ORDER BY dt_created DESC) as rn
            FROM rms_rtt_wf
        ) t6 ON t1.orn_no = t6.orn_no AND t6.rn = 1
        WHERE (effective_orn_dt_fr IS NULL OR CAST(t1.orn_dt AS DATE) >= effective_orn_dt_fr)
          AND (effective_orn_dt_to IS NULL OR CAST(t1.orn_dt AS DATE) <= effective_orn_dt_to)
          AND (i_orn_no IS NULL OR t1.orn_no LIKE '%' || i_orn_no || '%')
          AND (i_rcpt_no IS NULL OR COALESCE(online_rcpt.rcpt_no, otc_rcpt.rcpt_no) LIKE '%' || i_rcpt_no || '%')
          AND (i_txn_id IS NULL OR t5.pg_txn_id LIKE '%' || i_txn_id || '%')
          AND (i_refund_slip_no IS NULL OR t6.refund_slip_no LIKE '%' || i_refund_slip_no || '%')
          AND (i_rtt_app_no IS NULL OR t6.rtt_app_no LIKE '%' || i_rtt_app_no || '%')
          AND (i_created_by IS NULL OR t1.created_by LIKE '%' || i_created_by || '%')
          AND (i_order_status IS NULL OR t1.order_status = i_order_status)
          AND (i_rms_type IS NULL OR t1.rms_type = i_rms_type)
        ORDER BY t1.orn_dt DESC         

        LET o_date_expiry = (
            SELECT date_expiry
            FROM rms_rtt
            WHERE refund_slip_no = o_refund_slip_no
              AND dt_created = (SELECT MAX(dt_created) FROM rms_rtt WHERE refund_slip_no = o_refund_slip_no)
        );

        RETURN o_orn_no, o_refund_slip_no, o_orn_dt, o_txn_id, o_total_amt, 
               o_rms_type, o_order_status, o_rcpt_no, o_rtt_app_no, o_date_expiry, 
               o_rtt_status, total, o_mtt_id WITH RESUME;
    END FOREACH;

END IF;

END PROCEDURE;
GO

DROP PROCEDURE sp_getrttslippdf;
CREATE OR REPLACE PROCEDURE sp_getrttslippdf
(
    rtt_wf_id_input INTEGER
)
RETURNING 
    INTEGER AS rtt_wf_id,   
    NVARCHAR(255) AS rtt_app_no,  
    NVARCHAR(255) AS refund_slip_no,     
    NVARCHAR(255) AS cust_nm,     
    NVARCHAR(40) AS ent_no,    
    NVARCHAR(15) AS cust_phone, 
    NVARCHAR(255) AS cust_email,  
    NVARCHAR(10) AS rms_type,  
    NVARCHAR(50) AS refund_ty, 
    NVARCHAR(50) AS cust_state,  
    NVARCHAR(255) AS refund_reason,  
    NVARCHAR(255) AS rcpt_no,  
    NVARCHAR(255) AS orn_no,   
    NVARCHAR(255) AS txn_id,   
    DECIMAL(16,2) AS refund_total_amt,
    NVARCHAR(10) AS rtt_status

    DEFINE o_rtt_wf_id INTEGER;
    DEFINE o_rtt_app_no NVARCHAR(255);
    DEFINE o_refund_slip_no NVARCHAR(255);
    DEFINE o_cust_nm NVARCHAR(255);
    DEFINE o_ent_no NVARCHAR(255);
    DEFINE o_cust_phone NVARCHAR(15);
    DEFINE o_cust_email NVARCHAR(255);
    DEFINE o_rms_type NVARCHAR(10);
    DEFINE o_refund_ty NVARCHAR(50);
    DEFINE o_cust_state NVARCHAR(50);
    DEFINE o_refund_reason NVARCHAR(255);
    DEFINE o_rcpt_no NVARCHAR(255);
    DEFINE o_orn_no NVARCHAR(255);
    DEFINE o_txn_id NVARCHAR(255);
    DEFINE o_refund_total_amt DECIMAL(16,2);
    DEFINE v_rtt_status NVARCHAR(255);
    DEFINE v_largest_wf_hist_id INTEGER;

SELECT rtt_status
INTO v_rtt_status
FROM rms_rtt_wf
WHERE rtt_wf_id = rtt_wf_id_input;

IF v_rtt_status <> 'RG' THEN
    RETURN;
END IF;

SELECT FIRST 1 
    w.rtt_wf_id, 
    w.rtt_app_no, 
    w.refund_slip_no, 
    m.cust_nm, 
    w.ent_no, 
    m.cust_phone, 
    w.cust_email, 
    m.rms_type,
    w.refund_ty,
    m.cust_state,
    w.refund_reason,
    w.rcpt_no, 
    w.orn_no, 
    w.txn_id, 
    w.refund_total_amt,
    w.rtt_status
INTO 
    o_rtt_wf_id, 
    o_rtt_app_no, 
    o_refund_slip_no, 
    o_cust_nm, 
    o_ent_no, 
    o_cust_phone, 
    o_cust_email, 
    o_rms_type,
    o_refund_ty,
    o_cust_state,
    o_refund_reason,
    o_rcpt_no, 
    o_orn_no, 
    o_txn_id, 
    o_refund_total_amt,
    v_rtt_status
FROM 
    rms_rtt_wf w
JOIN 
    rms_rtt_item i
ON 
    w.rtt_wf_id = i.rtt_wf_id
JOIN 
    rms_mtt m
ON 
    w.orn_no = m.orn_no
WHERE 
    w.rtt_wf_id = rtt_wf_id_input;

RETURN 
    o_rtt_wf_id, 
    o_rtt_app_no, 
    o_refund_slip_no, 
    o_cust_nm, 
    o_ent_no, 
    o_cust_phone, 
    o_cust_email, 
    o_rms_type,
    o_refund_ty,
    o_cust_state,
    o_refund_reason,
    o_rcpt_no, 
    o_orn_no, 
    o_txn_id, 
    o_refund_total_amt,
    v_rtt_status;
END PROCEDURE;
GO

DROP PROCEDURE sp_insfmsapia;
CREATE OR REPLACE PROCEDURE sp_insfmsapia(
    i_ext_sys            VARCHAR(50),
    i_vendor_id          VARCHAR(50),   -- from fmsapia.getVendor_id()
    i_vendor_nm          VARCHAR(100),  -- from fmsapia.getVendor_nm()
    i_id_ty              VARCHAR(50),   -- from fmsapia.getId_ty()
    i_id_no              VARCHAR(50),   -- from fmsapia.getId_no()
    i_pm                 VARCHAR(50),   -- from fmsapia.getPm()
    i_p_desc             VARCHAR(200),  -- from fmsapia.getP_desc()
    i_p_id               VARCHAR(50),   -- from fmsapia.getP_id()
    i_p_bankname         VARCHAR(200),  -- from fmsapia.getP_bankname()
    i_p_value            VARCHAR(200),  -- from fmsapia.getP_value()
    i_addr1              VARCHAR(200),  -- from fmsapia.getAddr1()
    i_addr2              VARCHAR(200),  -- from fmsapia.getAddr2()
    i_addr3              VARCHAR(200),  -- from fmsapia.getAddr3()
    i_city               VARCHAR(100),  -- from fmsapia.getCity()
    i_country            VARCHAR(50),   -- from fmsapia.getCountry()
    i_postcode           VARCHAR(20),   -- from fmsapia.getPostcode()
    i_state              VARCHAR(50),   -- from fmsapia.getState()
    i_email              VARCHAR(100),  -- from fmsapia.getEmail()
    i_phone              VARCHAR(30),   -- from fmsapia.getPhone()
    i_rtt_app_no         VARCHAR(50),   -- from fmsapia.getRtt_app_no()
    i_refund_slip_no     VARCHAR(50),   -- from fmsapia.getRefund_slip_no()
    i_refund_total_amt   DECIMAL(15,2)  -- from fmsapia.getRefund_total_amt()
)
RETURNING INT;  -- will return the new fms_apia_ih_id

DEFINE i_fms_apia_v_id  INT;
DEFINE i_fms_apia_ih_id INT;
DEFINE o_result        INT;
DEFINE o_Description1  NVARCHAR(255);
DEFINE o_Description2  NVARCHAR(255);
DEFINE o_Description3  NVARCHAR(255);
DEFINE o_PIID1  NVARCHAR(10);
DEFINE o_PIID2  NVARCHAR(10);
DEFINE o_PIID3  NVARCHAR(10);
DEFINE o_PM1  NVARCHAR(50);
DEFINE o_PM2  NVARCHAR(50);
DEFINE o_PM3  NVARCHAR(50);
DEFINE o_Branch  NVARCHAR(50);
DEFINE o_Bankcode  NVARCHAR(50);
DEFINE o_Subaccount  NVARCHAR(50);
DEFINE v_refund_ty NVARCHAR(50);
--DEFINE v_bank_code NVARCHAR (50);


-- ensure rollback on error
ON EXCEPTION
    ROLLBACK WORK;
    LET o_result = -1;
    RETURN o_result;
END EXCEPTION;

BEGIN WORK;

-- one-by-one (simple and clear)
SELECT acct_cd INTO o_Description1 FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-Description1';
SELECT acct_cd INTO o_Description2 FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-Description2';
SELECT acct_cd INTO o_Description3 FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-Description3';

SELECT acct_cd INTO o_PIID1       FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentInstructionsID1';
SELECT acct_cd INTO o_PIID2       FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentInstructionsID2';
SELECT acct_cd INTO o_PIID3       FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentInstructionsID3';

SELECT acct_cd INTO o_PM1         FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentMethod1';
SELECT acct_cd INTO o_PM2         FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentMethod2';
SELECT acct_cd INTO o_PM3         FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-PaymentMethod3';

SELECT acct_cd INTO o_Branch      FROM rms_fms_acct WHERE acct_nm = 'FMS-Branch';
SELECT acct_cd INTO o_Bankcode    FROM rms_fms_acct WHERE acct_nm = 'FMS-APIA-BankCode';
SELECT acct_cd INTO o_Subaccount  FROM rms_fms_acct WHERE acct_nm = 'FMS-Subaccount';

SELECT refund_ty INTO v_refund_ty FROM rms_rtt_wf WHERE rtt_app_no = i_rtt_app_no;
IF (v_refund_ty = 'DA' OR v_refund_ty = 'RF') THEN
    LET i_refund_slip_no = i_rtt_app_no;
END IF;

--SELECT param_cd INTO v_bank_code FROM rms_param WHERE param_grp_nm = 'Common-BankName'
--AND nm_en = i_p_bankname;

INSERT INTO rms_fms_apia_v (
    ext_sys, vendor_id, vendor_nm,
    id_ty, id_no,
    pm, p_desc, p_id, p_value,
    addr1, addr2, addr3,
    city, country, postcode, state, email, phone,

    -- new 12 columns
    Description1, PaymentInstructionsID1, PaymentMethod1, Value1,
    Description2, PaymentInstructionsID2, PaymentMethod2, Value2,
    Description3, PaymentInstructionsID3, PaymentMethod3, Value3,
    created_by, modified_by, status
) VALUES (
    i_ext_sys, i_vendor_id, i_vendor_nm,
    i_id_ty, i_id_no,
    o_PM1, i_p_desc, i_p_id, i_p_value,
    i_addr1, i_addr2, i_addr3,
    i_city, i_country, i_postcode, i_state, i_email, i_phone,

    -- map variables
    o_Description1, o_PIID1, o_PM1, i_p_value,
    o_Description2, o_PIID2, o_PM2, i_vendor_nm,
    o_Description3, o_PIID3, o_PM3, i_p_bankname,
    'system', 'system', 'A'
);

  -- grab the generated vendor-PK
  SELECT MAX(fms_apia_v_id)
    INTO i_fms_apia_v_id
    FROM rms_fms_apia_v;

  -- 2) Invoice-header table
  INSERT INTO rms_fms_apia_ih (
      fms_apia_v_id,
      amt, ih_date, ih_desc, ih_hold, ih_type, vendor_ref,
      created_by, modified_by, status
  ) VALUES (
      i_fms_apia_v_id,
      i_refund_total_amt, CURRENT, 'RMS Refund', 1, 'Bill', i_refund_slip_no,
      'system', 'system', 'A'
  );

  -- grab the generated header-PK
  SELECT MAX(fms_apia_ih_id)
    INTO i_fms_apia_ih_id
    FROM rms_fms_apia_ih;

  -- return the header ID for your detail inserts
  LET o_result = i_fms_apia_ih_id;

  COMMIT WORK;
  RETURN o_result;
END PROCEDURE;
GO

DROP PROCEDURE sp_insrefund_ss;
CREATE OR REPLACE PROCEDURE sp_insrefund_ss    
(
    i_rcpt_no NVARCHAR(50), 
    i_rcpt_date DATETIME YEAR TO SECOND, 
    i_orn_no NVARCHAR(50), 
    i_refund_amt DECIMAL(16, 2),
    i_cust_email NVARCHAR(255),
    i_sme_email NVARCHAR(255),
    i_remark LVARCHAR(500),
    i_appeal_reason LVARCHAR(500) --for appeal count >= 1
)
RETURNING NVARCHAR(255);

DEFINE o_result int;
DEFINE o_row_count INT;
DEFINE v_rtt_wf_id INT;
DEFINE v_rtt_wf_hist_id INT;
DEFINE v_record_exists INT;
DEFINE v_appeal_cnt INT;
DEFINE v_rtt_status NVARCHAR(255);
DEFINE v_rtt_app_no NVARCHAR(255);
DEFINE v_assign_to NVARCHAR(25);
DEFINE v_refund_request_exist INT;
DEFINE v_rms_type NVARCHAR(25);
DEFINE v_max_appeal_cnt INT;
DEFINE v_mtt_id INT;
DEFINE v_dt_process DATETIME YEAR TO SECOND;
DEFINE v_branch_cd NVARCHAR(255);
DEFINE v_txn_id NVARCHAR(50);
DEFINE v_appeal_reason_raw LVARCHAR(500);

-- normalize early
LET v_appeal_reason_raw = i_appeal_reason;
LET i_appeal_reason = NVL(TRIM(i_appeal_reason), '');


SELECT rms_type, mtt_id INTO v_rms_type, v_mtt_id FROM rms_mtt where orn_no = i_orn_no;

IF v_rms_type IS NULL or v_rms_type = '' THEN
    RETURN -1;
ELSE 
    IF v_rms_type = 'Online' THEN

        SELECT COUNT(*) INTO v_refund_request_exist
        FROM rms_mtt a 
        LEFT JOIN ( SELECT mtt_id, rcpt_no, rcpt_dt FROM rms_mtt_rcpt
                              UNION ALL
                    SELECT o.mtt_id, orc.rcpt_no, orc.rcpt_dt FROM rms_otc_rcpt AS orc
                              JOIN rms_otc      AS o ON orc.otc_id = o.otc_id ) AS b ON a.mtt_id = b.mtt_id 
        WHERE 
            a.order_status = 'P' AND
            a.orn_no = i_orn_no AND
            a.cust_email = i_cust_email AND
            a.total_amt >= i_refund_amt AND
            b.rcpt_no = i_rcpt_no AND
            EXTEND(b.rcpt_dt, YEAR TO DAY)  = EXTEND(i_rcpt_date, YEAR TO DAY);

        SELECT pymt_submit_dt, pg_txn_id INTO v_dt_process, v_txn_id FROM rms_mtt_pg
        WHERE mtt_id = v_mtt_id;
        
        LET v_branch_cd = 'HQ';
       
    END IF;        

    IF v_rms_type = 'OTC' THEN

        SELECT COUNT(*) INTO v_refund_request_exist
        FROM rms_mtt a 
        LEFT JOIN ( SELECT mtt_id, rcpt_no, rcpt_dt FROM rms_mtt_rcpt
                              UNION ALL
                    SELECT o.mtt_id, orc.rcpt_no, orc.rcpt_dt FROM rms_otc_rcpt AS orc
                              JOIN rms_otc      AS o ON orc.otc_id = o.otc_id ) AS b ON a.mtt_id = b.mtt_id 
        LEFT JOIN rms_otc c on a.mtt_id = c.mtt_id 
        WHERE 
            a.order_status = 'P' AND
            a.orn_no = i_orn_no AND
            a.cust_email = i_cust_email AND
            a.total_amt >= i_refund_amt AND
            b.rcpt_no = i_rcpt_no AND
            EXTEND(b.rcpt_dt, YEAR TO DAY)  = EXTEND(i_rcpt_date, YEAR TO DAY);
           
        SELECT dt_created INTO v_dt_process FROM rms_otc
        WHERE mtt_id = v_mtt_id;


        SELECT b.branch_cd INTO v_branch_cd FROM rms_otc a JOIN rms_otc_counter b
        ON a.otc_counter_id = b.otc_counter_id WHERE mtt_id = v_mtt_id;

        LET v_txn_id = '';
     END IF;
END IF;

IF v_refund_request_exist = 0 THEN
    RETURN -1; 
END IF;


    -- Check if ORN exists
SELECT COUNT(*) INTO v_record_exists
FROM rms_rtt_wf
WHERE orn_no = i_orn_no;

    IF v_record_exists > 0 THEN
        -- Fetch maximum appeal count
        SELECT MAX(appeal_cnt) INTO v_max_appeal_cnt
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no;

        -- Fetch status for the max appeal count
        SELECT rtt_status INTO v_rtt_status
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no
          AND appeal_cnt = v_max_appeal_cnt;

        -- Validate appeal
        IF v_rtt_status = 'RR' AND v_max_appeal_cnt < 2 THEN
            IF i_appeal_reason != '' THEN
                 SELECT appeal_cnt into v_appeal_cnt 
                 FROM rms_rtt_wf 
                 WHERE orn_no = i_orn_no and appeal_cnt = v_max_appeal_cnt;
                LET v_appeal_cnt = v_appeal_cnt + 1;
               LET i_remark = NVL(i_remark, '') || ' - [Appeal Reason: ' || TRIM(i_appeal_reason) || ']';
            END IF;
            IF i_appeal_reason = '' THEN
                RETURN -2 ; -- Error code for missing reason message
            END IF;
        ELSE
            RETURN -3; -- Error code for invalid status or max appeals reached
        END IF;
    ELSE
    -- New refund request (v_record_exists = 0)
            IF v_appeal_reason_raw IS NOT NULL THEN
                IF TRIM(v_appeal_reason_raw) = '' THEN
                    RETURN -5;  -- Empty string not accepted (general rule)
                ELSE
                    RETURN -4;  -- New Refund Request not required appeal reason
                END IF;
            END IF;

            LET v_appeal_cnt = 0;  -- proceed as new
    END IF;



-- Generate new RTT application number
    EXECUTE PROCEDURE sp_getrunno('RT') into o_result;
 
    /* UPDATE last running no */
    UPDATE rms_runno
    SET last_runno = last_runno +1
    WHERE runno_cd = 'RT';

    /* Select running no */
	SELECT last_runno
	INTO o_result
	FROM rms_runno
	WHERE runno_cd = 'RT';

	-- Generate unique receipt number
	LET v_rtt_app_no = 'RT' || TO_CHAR(TODAY, '%Y%m%d') || LPAD(o_result, 6, '0');
    
    SELECT role_name into v_assign_to 
    FROM rms_param 
    WHERE param_cd = 'PFA';

    INSERT INTO rms_rtt_wf
    (
        rcpt_no, 
        rcpt_date, 
        orn_no, 
        txn_id, 
        cust_email, 
        sme_email, 
        created_by, 
        modified_by,
        appeal_cnt,
        rtt_status,
        refund_ty,
        refund_total_amt,
        rtt_app_no,
        assign_to,
        refund_reason,
        requested_by,
        dt_requested,
        dt_process, 
        branch_cd
    )
    VALUES
    (
        i_rcpt_no, 
        i_rcpt_date,
        i_orn_no, 
        v_txn_id, 
        i_cust_email, 
        i_sme_email,
        'Anonymous',
        'Anonymous',
        v_appeal_cnt,
        'PFA',
        'RS01',
        i_refund_amt,
        v_rtt_app_no,
        v_assign_to,
        null,
        'Anonymous',
         CURRENT,
         v_dt_process, 
         v_branch_cd
    );

    LET v_rtt_wf_id = DBINFO('sqlca.sqlerrd1');

    INSERT INTO rms_rtt_wf_hist
    (
        rtt_wf_id, 
        rcpt_no, 
        rcpt_date, 
        orn_no, 
        txn_id, 
        cust_email, 
        sme_email, 
        created_by, 
        modified_by,
        appeal_cnt,
        rtt_status,
        refund_ty,
        refund_total_amt,
        rtt_app_no,
        action,
        dt_action,
        assign_to
    )
    VALUES
    (
        v_rtt_wf_id,  
        i_rcpt_no, 
        i_rcpt_date,
        i_orn_no, 
        v_txn_id, 
        i_cust_email, 
        i_sme_email,
        'Anonymous', 
        'Anonymous',
        v_appeal_cnt,
        'PFA',
        'RS01',
        i_refund_amt,
        v_rtt_app_no,
        'Refund Request',
        CURRENT,
        v_assign_to
    );

    LET v_rtt_wf_hist_id  = DBINFO('sqlca.sqlerrd1');

    INSERT INTO rms_rtt_wf_msg
    (   
        rtt_wf_hist_id,
        rtt_wf_id,
        msg,
        created_by,
        modified_by
    )
    VALUES
    (
        v_rtt_wf_hist_id,
        v_rtt_wf_id,  
        i_remark,
        'Anonymous',
        'Anonymous'
    );


    LET o_row_count = DBINFO('sqlca.sqlerrd1');
    RETURN v_mtt_id || '|' || v_rtt_wf_id || '|' || v_rtt_app_no;

END PROCEDURE;
GO

DROP PROCEDURE sp_insrttwf;
CREATE OR REPLACE PROCEDURE sp_insrttwf(
    i_rcpt_no NVARCHAR(255),
    i_rcpt_date DATETIME YEAR TO SECOND,
    i_orn_no NVARCHAR(255),
    i_txn_id NVARCHAR(255),
    i_refund_amt DECIMAL(10, 2),
    i_ent_no NVARCHAR(40),
    i_ent_nm LVARCHAR(400),
    i_cust_email NVARCHAR(255),
    i_sme_email  NVARCHAR(255),
    i_requested_by NVARCHAR(255),
    i_created_by NVARCHAR(255),
    i_modified_by NVARCHAR(255),
    i_msg LVARCHAR(500),
    i_refund_cd NVARCHAR(255),
    i_assign_to NVARCHAR(255),
    i_rtt_status NVARCHAR(255),
    i_refund_ty NVARCHAR(255),
    i_refund_reason NVARCHAR(255)
) RETURNING INT;

    DEFINE o_row_count INT;
    DEFINE v_max_appeal_cnt INT;
    DEFINE v_appeal_cnt INT;
    DEFINE v_rtt_status NVARCHAR(10);
    DEFINE record_exists INT;
    DEFINE o_result INT;
    DEFINE o_rtt_app_no NVARCHAR(255);
    DEFINE v_rtt_wf_id INT;
    DEFINE v_rtt_wf_hist_id INT;
    DEFINE v_assign_to NVARCHAR(255);
    DEFINE o_runno int;
    DEFINE v_rms_type NVARCHAR(255);
    DEFINE v_dt_process DATETIME YEAR TO SECOND;
    DEFINE v_mtt_id INT;
    DEFINE v_branch_cd NVARCHAR(255);

    ON EXCEPTION
     ROLLBACK WORK;
    END EXCEPTION;

    BEGIN WORK;

    -- Validate ORN number input
    IF TRIM(i_orn_no) IS NULL OR i_orn_no = '' THEN
        RETURN -1; -- Error code for missing ORN number
    END IF;

    LET i_msg = TRIM(i_msg);

    -- Check if ORN exists
    SELECT COUNT(*) INTO record_exists
    FROM rms_rtt_wf
    WHERE orn_no = i_orn_no;

    IF record_exists > 0 THEN
        -- Fetch maximum appeal count
        SELECT MAX(appeal_cnt) INTO v_max_appeal_cnt
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no;

        -- Fetch status for the max appeal count
        SELECT rtt_status INTO v_rtt_status
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no
          AND appeal_cnt = v_max_appeal_cnt;

        -- Validate appeal
        IF v_rtt_status = 'RR' AND v_max_appeal_cnt < 2 THEN
                IF i_refund_ty = 'CB' THEN
                    -- For 'CB', always select and increment appeal_cnt, regardless of i_msg
                    SELECT appeal_cnt INTO v_appeal_cnt
                    FROM rms_rtt_wf
                    WHERE orn_no = i_orn_no AND appeal_cnt = v_max_appeal_cnt;

                    LET v_appeal_cnt = v_appeal_cnt + 1;
                ELSE
                    -- For other types (like 'RS02'), apply the original logic regarding i_msg
                    IF i_msg = '' OR i_msg IS NULL THEN -- It's good practice to check for NULL as well if i_msg can be NULL
                        RETURN -2; -- Error code for missing reason message
                    ELSE
                        -- i_msg is not empty, proceed to select and increment
                        SELECT appeal_cnt INTO v_appeal_cnt
                        FROM rms_rtt_wf
                        WHERE orn_no = i_orn_no AND appeal_cnt = v_max_appeal_cnt;

                        LET v_appeal_cnt = v_appeal_cnt + 1;
                        LET i_msg = 'Appeal Reason: ' || TRIM(i_msg) || '';
                    END IF;
                END IF;
        ELSE
            RETURN -3; -- Error code for invalid status or max appeals reached
        END IF;
    ELSE
        -- New refund request
        LET v_appeal_cnt = 0;

--        IF TRIM(i_msg) IS NOT NULL AND i_msg != '' THEN
--            RETURN -4; -- Error code for message provided in the first request
--        END IF;
    END IF;

    -- Generate new RTT application number
    EXECUTE PROCEDURE sp_getrunno('RT') into o_result;
 
    /* UPDATE last running no */
    UPDATE rms_runno
    SET last_runno = last_runno +1
    WHERE runno_cd = 'RT';

    /* Select running no */
	SELECT last_runno
	INTO o_result
	FROM rms_runno
	WHERE runno_cd = 'RT';

	-- Generate unique receipt number
	LET o_rtt_app_no = 'RT' || TO_CHAR(TODAY, '%Y%m%d') || LPAD(o_result, 6, '0');

-- i_assign_to is null then check the param table ( user_role )
IF i_assign_to is NULL then
    SELECT role_name into v_assign_to 
    FROM rms_param 
    WHERE param_cd = i_rtt_status;
ELSE 
    LET v_assign_to = i_assign_to;
END IF;

SELECT mtt_id, rms_type INTO v_mtt_id, v_rms_type FROM rms_mtt 
WHERE orn_no = i_orn_no;

IF v_rms_type = 'OTC' THEN

    
    SELECT dt_created INTO v_dt_process FROM rms_otc
    WHERE mtt_id = v_mtt_id;

    
    SELECT b.branch_cd INTO v_branch_cd FROM rms_otc a JOIN rms_otc_counter b
    ON a.otc_counter_id = b.otc_counter_id WHERE mtt_id = v_mtt_id;


    ELSE IF v_rms_type = 'ONLINE' THEN
    
    
    SELECT pymt_submit_dt INTO v_dt_process FROM rms_mtt_pg
    WHERE mtt_id = v_mtt_id and pg_txn_id = i_txn_id;

   
     LET v_branch_cd = 'HQ';

     END IF;
END IF;

    -- Insert into rms_rtt_wf
    INSERT INTO rms_rtt_wf (
        orn_no, txn_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested , 
        refund_cd, assign_to, refund_reason, dt_process, branch_cd
    ) VALUES (
       i_orn_no, i_txn_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, CURRENT, 
        i_refund_cd, v_assign_to, i_refund_reason, v_dt_process, v_branch_cd
    );

    LET v_rtt_wf_id = DBINFO('sqlca.sqlerrd1');



    -- Insert into rtt_wf_hist table
    INSERT INTO rms_rtt_wf_hist (
      orn_no, txn_id, rtt_wf_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested, action, dt_action, assign_to
    ) VALUES (
      i_orn_no, i_txn_id, v_rtt_wf_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, 
        CURRENT, 'Refund Request', CURRENT, v_assign_to
    );

LET v_rtt_wf_hist_id = DBINFO('sqlca.sqlerrd1');
    -- Insert into message table
    INSERT INTO rms_rtt_wf_msg (
        rtt_wf_id, rtt_wf_hist_id, msg, created_by, modified_by
    ) VALUES (
        v_rtt_wf_id,v_rtt_wf_hist_id, i_msg, i_created_by, i_modified_by
    );

COMMIT WORK;

-- Return success
RETURN v_rtt_wf_id;
END PROCEDURE;
GO

DROP PROCEDURE sp_insrttwf_da;
CREATE OR REPLACE PROCEDURE sp_insrttwf_da(
    i_rcpt_no NVARCHAR(255),
    i_rcpt_date DATETIME YEAR TO SECOND,
    i_orn_no NVARCHAR(255),
    i_txn_id NVARCHAR(255),
    i_refund_amt DECIMAL(10, 2),
    i_ent_no NVARCHAR(40),
    i_ent_nm LVARCHAR(400),
    i_cust_email NVARCHAR(255),
    i_sme_email  NVARCHAR(255),
    i_requested_by NVARCHAR(255),
    i_created_by NVARCHAR(255),
    i_modified_by NVARCHAR(255),
    i_msg LVARCHAR(500),
    i_refund_cd NVARCHAR(255),
    i_assign_to NVARCHAR(255),
    i_rtt_status NVARCHAR(255),
    i_refund_ty NVARCHAR(255),
    i_refund_reason NVARCHAR(255),

    i_identity_type NVARCHAR(255),
    i_identity_number NVARCHAR(255),
    i_bank_account_no NVARCHAR(255),
    i_bank_account_name NVARCHAR(255),
    i_bank_account_type NVARCHAR(255),
    i_bank_holder_name NVARCHAR(255),
    i_billing_address_1 NVARCHAR(255),
    i_billing_address_2 NVARCHAR(255),
    i_billing_address_3 NVARCHAR(255),
    i_city NVARCHAR(255),
    i_postcode NVARCHAR(255),
    i_state NVARCHAR(255),
    i_rec_email NVARCHAR(255)
) RETURNING INT;

    DEFINE o_row_count INT;
    DEFINE v_max_appeal_cnt INT;
    DEFINE v_appeal_cnt INT;
    DEFINE v_rtt_status NVARCHAR(10);
    DEFINE record_exists INT;
    DEFINE o_result INT;
    DEFINE o_rtt_app_no NVARCHAR(255);
    DEFINE v_rtt_wf_id INT;
    DEFINE v_rtt_wf_hist_id INT;
    DEFINE v_assign_to NVARCHAR(255);
    DEFINE o_runno  int;
    DEFINE v_rms_type NVARCHAR(255);
    DEFINE v_dt_process DATETIME YEAR TO SECOND;
    DEFINE v_mtt_id INT;
    DEFINE v_branch_cd NVARCHAR(255);

    ON EXCEPTION
     ROLLBACK WORK;
    END EXCEPTION;

    BEGIN WORK;

    -- Validate ORN number input
    IF TRIM(i_orn_no) IS NULL OR i_orn_no = '' THEN
        RETURN -1; -- Error code for missing ORN number
    END IF;

    -- Check if ORN exists
    SELECT COUNT(*) INTO record_exists
    FROM rms_rtt_wf
    WHERE orn_no = i_orn_no;

    IF record_exists > 0 THEN
        -- Fetch maximum appeal count
        SELECT MAX(appeal_cnt) INTO v_max_appeal_cnt
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no;

        -- Fetch status for the max appeal count
        SELECT rtt_status INTO v_rtt_status
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no
          AND appeal_cnt = v_max_appeal_cnt;

        -- Validate appeal
        IF ( v_rtt_status = 'RR') AND v_max_appeal_cnt < 2 THEN

                   
                 SELECT appeal_cnt into v_appeal_cnt 
                 FROM rms_rtt_wf 
                 WHERE orn_no = i_orn_no and appeal_cnt = v_max_appeal_cnt;

                    IF( v_rtt_status = 'RR') THEN
                            LET v_appeal_cnt = v_appeal_cnt + 1;
                    ELSE  
                            LET v_appeal_cnt = v_appeal_cnt;
                 END IF;


        ELSE
            RETURN -3; -- Error code for invalid status or max appeals reached
        END IF;
    ELSE
        -- New refund request
        LET v_appeal_cnt = 0;

--        IF TRIM(i_msg) IS NOT NULL AND i_msg != '' THEN
--            RETURN -4; -- Error code for message provided in the first request
--        END IF;
    END IF;

    -- Generate new RTT application number
    EXECUTE PROCEDURE sp_getrunno('RT') into o_result;
 
    /* UPDATE last running no */
    UPDATE rms_runno
    SET last_runno = last_runno +1
    WHERE runno_cd = 'RT';

    /* Select running no */
	SELECT last_runno
	INTO o_result
	FROM rms_runno
	WHERE runno_cd = 'RT';

	-- Generate unique receipt number
	LET o_rtt_app_no = 'RT' || TO_CHAR(TODAY, '%Y%m%d') || LPAD(o_result, 6, '0');


-- i_assign_to is null then check the param table ( user_role )
IF i_assign_to is NULL then
    SELECT role_name into v_assign_to 
    FROM rms_param 
    WHERE param_cd = i_rtt_status;
ELSE 
    LET v_assign_to = i_assign_to;
END IF;

SELECT mtt_id, rms_type INTO v_mtt_id, v_rms_type FROM rms_mtt 
WHERE orn_no = i_orn_no;

IF v_rms_type = 'OTC' THEN


    
    SELECT dt_created INTO v_dt_process FROM rms_otc
    WHERE mtt_id = v_mtt_id;

    
    SELECT b.branch_cd INTO v_branch_cd FROM rms_otc a JOIN rms_otc_counter b
    ON a.otc_counter_id = b.otc_counter_id WHERE mtt_id = v_mtt_id;

    ELSE IF v_rms_type = 'ONLINE' THEN
    
    
    SELECT pymt_submit_dt INTO v_dt_process FROM rms_mtt_pg
    WHERE mtt_id = v_mtt_id and pg_txn_id = i_txn_id;

     
     LET v_branch_cd = 'HQ';

     END IF;
END IF;

    -- Insert into rms_rtt_wf
    INSERT INTO rms_rtt_wf (
        orn_no, txn_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested , refund_cd, assign_to, refund_reason, dt_process, branch_cd
    ) VALUES (
       i_orn_no, i_txn_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, CURRENT, 
        i_refund_cd, v_assign_to, i_refund_reason, v_dt_process, v_branch_cd
    );

    LET v_rtt_wf_id = DBINFO('sqlca.sqlerrd1');

    INSERT INTO rms_rtt_form (
       rtt_wf_id, id_ty, id_no, bank_acc_no, bank_acc_nm, acc_ty, acc_holder_nm, bill_addr_1, bill_addr_2, bill_addr_3, rec_email, bill_postcode, bill_city, bill_state, created_by, modified_by
    )VALUES(
        v_rtt_wf_id, i_identity_type, i_identity_number, i_bank_account_no, i_bank_account_name, i_bank_account_type, i_bank_holder_name, i_billing_address_1, i_billing_address_2, i_billing_address_3, i_rec_email, i_postcode, i_city, i_state,  i_created_by, i_modified_by
    );

    -- Insert into rtt_wf_hist table
    INSERT INTO rms_rtt_wf_hist (
      orn_no, txn_id, rtt_wf_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested, action, dt_action, assign_to
    ) VALUES (
      i_orn_no, i_txn_id, v_rtt_wf_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, 
        CURRENT, 'Refund Request', CURRENT, v_assign_to
    );

LET v_rtt_wf_hist_id = DBINFO('sqlca.sqlerrd1');
    -- Insert into message table
    INSERT INTO rms_rtt_wf_msg (
        rtt_wf_id, rtt_wf_hist_id, msg, created_by, modified_by
    ) VALUES (
        v_rtt_wf_id,v_rtt_wf_hist_id, i_msg, i_created_by, i_modified_by
    );

COMMIT WORK;
-- Return success
RETURN v_rtt_wf_id;
END PROCEDURE;
GO


DROP PROCEDURE sp_insrttwf_rf;
CREATE OR REPLACE PROCEDURE sp_insrttwf_rf(
    i_rcpt_no NVARCHAR(255),
    i_rcpt_date DATETIME YEAR TO SECOND,
    i_orn_no NVARCHAR(255),
    i_txn_id NVARCHAR(255),
    i_refund_amt DECIMAL(10, 2),
    i_ent_no NVARCHAR(40),
    i_ent_nm LVARCHAR(400),
    i_ent_ty NVARCHAR (255), -- new add
    i_cust_nm NVARCHAR (255), -- new add
    i_cust_phone NVARCHAR(15), -- new add
    i_rcpt_amt DECIMAL(10, 2),  -- new add
    i_cust_email NVARCHAR(255),
    i_sme_email  NVARCHAR(255),
    i_requested_by NVARCHAR(255),
    i_created_by NVARCHAR(255),
    i_modified_by NVARCHAR(255),
    i_msg LVARCHAR(500),
    i_refund_cd NVARCHAR(255),
    i_assign_to NVARCHAR(255),
    i_rtt_status NVARCHAR(255),
    i_refund_ty NVARCHAR(255),
    i_refund_reason NVARCHAR(255),

    i_identity_type NVARCHAR(255),
    i_identity_number NVARCHAR(255),
    i_bank_account_no NVARCHAR(255),
    i_bank_account_name NVARCHAR(255),
    i_bank_account_type NVARCHAR(255),
    i_bank_holder_name NVARCHAR(255),
    i_billing_address_1 NVARCHAR(255),
    i_billing_address_2 NVARCHAR(255),
    i_billing_address_3 NVARCHAR(255),
    i_city NVARCHAR(255),
    i_postcode NVARCHAR(255),
    i_state NVARCHAR(255),
    i_rec_email NVARCHAR(255)
) RETURNING INT;

    DEFINE o_row_count INT;
    DEFINE v_max_appeal_cnt INT;
    DEFINE v_appeal_cnt INT;
    DEFINE v_rtt_status NVARCHAR(10);
    DEFINE record_exists INT;
    DEFINE o_result INT;
    DEFINE o_rtt_app_no NVARCHAR(255);
    DEFINE v_rtt_wf_id INT;
    DEFINE v_rtt_wf_hist_id INT;
    DEFINE v_assign_to NVARCHAR(255);
    DEFINE o_runno int;
    DEFINE v_state_cd    NVARCHAR(30);
    -- Validate ORN number input
    IF TRIM(i_orn_no) IS NULL OR i_orn_no = '' THEN
        RETURN -1; -- Error code for missing ORN number
    END IF;

    -- Check if ORN exists
    SELECT COUNT(*) INTO record_exists
    FROM rms_rtt_wf
    WHERE orn_no = i_orn_no;

    IF record_exists > 0 THEN
        -- Fetch maximum appeal count
        SELECT MAX(appeal_cnt) INTO v_max_appeal_cnt
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no;

        -- Fetch status for the max appeal count
        SELECT rtt_status INTO v_rtt_status
        FROM rms_rtt_wf
        WHERE orn_no = i_orn_no
          AND appeal_cnt = v_max_appeal_cnt;

        -- Validate appeal
        IF v_rtt_status = 'RR' AND v_max_appeal_cnt < 3 THEN
         
                LET v_appeal_cnt = v_appeal_cnt + 1;

        ELSE
            RETURN -3; -- Error code for invalid status or max appeals reached
        END IF;
    ELSE
        -- New refund request
        LET v_appeal_cnt = 0;

--        IF TRIM(i_msg) IS NOT NULL AND i_msg != '' THEN
--            RETURN -4; -- Error code for message provided in the first request
--        END IF;
    END IF;

    -- Generate new RTT application number
    EXECUTE PROCEDURE sp_getrunno('RT') into o_result;
 
    /* UPDATE last running no */
    UPDATE rms_runno
    SET last_runno = last_runno +1
    WHERE runno_cd = 'RT';

    /* Select running no */
	SELECT last_runno
	INTO o_result
	FROM rms_runno
	WHERE runno_cd = 'RT';

	-- Generate unique receipt number
	LET o_rtt_app_no = 'RT' || TO_CHAR(TODAY, '%Y%m%d') || LPAD(o_result, 6, '0');

-- i_assign_to is null then check the param table ( user_role )
IF i_assign_to is NULL then
    SELECT role_name into v_assign_to 
    FROM rms_param 
    WHERE param_cd = i_rtt_status;
ELSE 
    LET v_assign_to = i_assign_to;
END IF;

SELECT param_cd INTO v_state_cd
FROM rms_param
WHERE param_grp_nm = 'State' AND (nm_en = i_state);    -- matches English name
    -- Insert into rms_rtt_wf
    INSERT INTO rms_rtt_wf (
        orn_no, txn_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested , refund_cd, assign_to, refund_reason
    ) VALUES (
       i_orn_no, i_txn_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, CURRENT, 
        i_refund_cd, v_assign_to, i_refund_reason
    );

    LET v_rtt_wf_id = DBINFO('sqlca.sqlerrd1');

    -- Insert into rms_rtt_form

    INSERT INTO rms_rtt_form (
       rtt_wf_id, id_ty, id_no, bank_acc_no, bank_acc_nm, acc_ty, acc_holder_nm, bill_addr_1, bill_addr_2, bill_addr_3, cust_email, rec_email, bill_postcode, bill_city, bill_state, created_by, modified_by, cust_nm, cust_phone, rcpt_no, rcpt_amt, orn_no, txn_id, entity_nm, entity_ty, entity_no
    )VALUES(
        v_rtt_wf_id, i_identity_type, i_identity_number, i_bank_account_no, i_bank_account_name, i_bank_account_type, i_bank_holder_name, i_billing_address_1, i_billing_address_2, i_billing_address_3,i_cust_email, i_cust_email, i_postcode, i_city, i_state,  i_created_by, i_modified_by, i_cust_nm, i_cust_phone,i_rcpt_no, i_rcpt_amt,i_orn_no, i_txn_id, i_ent_nm, i_ent_ty, i_ent_no
    );

    -- Insert into rtt_wf_hist table
    INSERT INTO rms_rtt_wf_hist (
      orn_no, txn_id, rtt_wf_id, rtt_app_no, rcpt_no, rcpt_date, refund_total_amt,
        ent_no, ent_nm, cust_email, created_by, modified_by,
        appeal_cnt, rtt_status, refund_ty, sme_email, requested_by,dt_requested, action, dt_action, assign_to
    ) VALUES (
      i_orn_no, i_txn_id, v_rtt_wf_id, o_rtt_app_no, i_rcpt_no, i_rcpt_date, 
        i_refund_amt, i_ent_no, i_ent_nm, i_cust_email,
        i_created_by, i_modified_by, v_appeal_cnt, i_rtt_status, i_refund_ty, i_sme_email, i_requested_by, 
        CURRENT, 'Refund Request', CURRENT, v_assign_to
    );

LET v_rtt_wf_hist_id = DBINFO('sqlca.sqlerrd1');
    -- Insert into message table
    INSERT INTO rms_rtt_wf_msg (
        rtt_wf_id, rtt_wf_hist_id, msg, created_by, modified_by
    ) VALUES (
        v_rtt_wf_id,v_rtt_wf_hist_id, i_msg, i_created_by, i_modified_by
    );


-- Return success
RETURN v_rtt_wf_id;
END PROCEDURE;
GO

DROP PROCEDURE sp_updrttslippdf;
CREATE OR REPLACE PROCEDURE sp_updrttslippdf
(
    rtt_wf_id_input INTEGER
)
RETURNING INTEGER

    DEFINE v_rtt_status   NVARCHAR(10);
    DEFINE v_app_no       NVARCHAR(255);
    DEFINE v_orn_no       NVARCHAR(255);
    DEFINE v_txn_id       NVARCHAR(255);
    DEFINE v_rows_changed INTEGER;
    DEFINE v_hist_exists INTEGER;

-- 0) Validate WF exists
IF (SELECT COUNT(*) FROM rms_rtt_wf WHERE rtt_wf_id = rtt_wf_id_input) = 0 THEN
    RETURN -1;
END IF;

-- 1) Read current status & bits we need for history
SELECT rtt_status, rtt_app_no, orn_no, txn_id
  INTO v_rtt_status, v_app_no, v_orn_no, v_txn_id
  FROM rms_rtt_wf
 WHERE rtt_wf_id = rtt_wf_id_input;

-- 2) Only finalize if still RG
IF v_rtt_status <> 'RG' THEN
    RETURN 0;
END IF;

-- 3) Flip WF to REG only if still RG (idempotent)
UPDATE rms_rtt_wf
   SET rtt_status = 'REG'
 WHERE rtt_wf_id = rtt_wf_id_input
   AND rtt_status = 'RG';

-- 4) Cascade update to rms_rtt
UPDATE rms_rtt
   SET rtt_status = 'REG',
       date_expiry = TODAY + 181  -- (180 + 1)
 WHERE rtt_app_no = v_app_no;


SELECT COUNT(*)
  INTO v_hist_exists
  FROM rms_rtt_wf_hist
 WHERE rtt_wf_id = rtt_wf_id_input
   AND action    = 'Refund Email Sent';

IF v_hist_exists = 0 THEN
    INSERT INTO rms_rtt_wf_hist
        (rtt_wf_id, rtt_app_no, orn_no, txn_id, rtt_status,
         dt_created, dt_modified, created_by, modified_by, status,
         requested_by, action, dt_action, assign_to)
    VALUES
        (rtt_wf_id_input, v_app_no, v_orn_no, v_txn_id, 'REG',
         CURRENT, CURRENT, 'SYSTEM', 'SYSTEM', 'A',
         'SYSTEM', 'Refund Email Sent', CURRENT, NULL);
END IF;

RETURN 1;
END PROCEDURE;
GO

DROP PROCEDURE sp_insfmsapia_id;
CREATE OR REPLACE PROCEDURE sp_insfmsapia_id(
   i_fmsApiaIH_ID int,
   i_rtt_app_no NVARCHAR(30),
   i_unit_fee DECIMAL(16, 2),
   i_qty INT,
   i_item_desc NVARCHAR(255),
   i_item_ref_no NVARCHAR(255),
   i_net_amt DECIMAL(16, 2)
)
RETURNING INT;  -- will return the new fms_apia_ih_id
 
DEFINE o_fms_apia_id_id  INT;
DEFINE o_result        INT;
DEFINE v_refund_cd NVARCHAR(50);
DEFINE o_branch_cd NVARCHAR(50);
DEFINE o_Subaccount  NVARCHAR(50);
 
-- ensure rollback on error
ON EXCEPTION
    ROLLBACK WORK;
    LET o_result = -1;
    RETURN o_result;
END EXCEPTION;
 
BEGIN WORK;
 
SELECT refund_cd INTO v_refund_cd 
FROM rms_rtt_wf 
WHERE rtt_app_no  = i_rtt_app_no;
 
SELECT acct_cd INTO o_branch_cd      FROM rms_fms_acct WHERE acct_nm = 'FMS-Branch';
SELECT acct_cd INTO o_Subaccount  FROM rms_fms_acct WHERE acct_nm = 'FMS-Subaccount';
 
 
  -- 1) Vendor table
  INSERT INTO rms_fms_apia_id  (
     fms_apia_ih_id, acct, amt, branch, ex_cost, qty, sub_acct, tax_ca, txn_desc, unit, uom, created_by, modified_by, status, dt_created, dt_modified
) VALUES (
    i_fmsApiaIH_ID, v_refund_cd , i_net_amt, o_branch_cd, null, i_qty, o_Subaccount, null,  i_item_ref_no, i_unit_fee, null, 'system', 'system','A', CURRENT, CURRENT
  );
 
 
-- Check if the inserts were successful
LET o_result = DBINFO('sqlca.sqlerrd2');
 
-- Commit if no error occurred, else rollback
IF (o_result > 0) THEN
    COMMIT WORK;
    RETURN o_result;  -- Return the number of affected rows
ELSE
    ROLLBACK WORK;
    RETURN -1;  -- Return -1 if the insert failed
END IF;
 
END PROCEDURE;
GO

