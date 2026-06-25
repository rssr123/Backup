DROP PROCEDURE sp_insrttitem;
CREATE OR REPLACE PROCEDURE sp_insrttitem(
    i_rtt_wf_id INT,
    i_unit_fee DECIMAL(16, 2),
    i_qty INT,
    i_item_ref_no NVARCHAR(255),
    i_item_desc NVARCHAR(255),
    i_tax_pct DECIMAL(16, 2),
    i_tax_amt DECIMAL(16, 2),
    i_grant_cd NVARCHAR(50),
    i_disc_amt DECIMAL(16, 2),
    i_gross_amt DECIMAL(16, 2),
    i_created_by NVARCHAR(255),
    i_modified_by NVARCHAR(255),
    i_net_amt DECIMAL(16, 2),
    i_entity_no NVARCHAR(40),
    i_entity_nm LVARCHAR(400),
    i_entity_type NVARCHAR(1)
)
RETURNING INT AS rtt_item_id;

DEFINE o_rtt_item_id INT;
DEFINE o_exist INT;

 on exception
	rollback work;
end exception;

-- start a transaction
 BEGIN WORK;

-- Check if the item already exists in rms_rtt_item
SELECT COUNT(1)
INTO o_exist
FROM rms_rtt_item
WHERE rtt_wf_id = i_rtt_wf_id 
  AND item_ref_no = i_item_ref_no  
  AND i_item_ref_no != 'NON-RMS';

IF o_exist = 0 THEN
    -- Insert new item
    INSERT INTO rms_rtt_item(
        rtt_wf_id, 
        unit_fee, 
        qty, 
        item_ref_no, 
        item_desc,
        tax_pct, 
        tax_amt, 
        grant_cd, 
        disc_amt, 
        refund_amt,
        gross_amt,
        created_by, 
        modified_by, 
        dt_created, 
        dt_modified,
        net_amt,
        entity_no,
        entity_nm,
        entity_type
    )
    VALUES (
        i_rtt_wf_id, 
        i_unit_fee, 
        i_qty, 
        i_item_ref_no, 
        i_item_desc,
        i_tax_pct, 
        i_tax_amt, 
        i_grant_cd, 
        i_disc_amt, 
        i_net_amt,
        i_gross_amt,
        i_created_by, 
        i_modified_by, 
        CURRENT YEAR TO fraction(3), 
        CURRENT YEAR TO fraction(3),
        i_net_amt,
        i_entity_no,
        i_entity_nm,
        i_entity_type
    );

    -- Get the newly inserted rtt_item_id
    SELECT FIRST 1 rtt_item_id
    INTO o_rtt_item_id
    FROM rms_rtt_item
    WHERE rtt_wf_id = i_rtt_wf_id AND item_ref_no = i_item_ref_no;

    IF (o_rtt_item_id IS NULL OR o_rtt_item_id < 1) THEN
        LET o_rtt_item_id = -1; -- Indicate failure
    END IF;
ELSE
    -- Update existing item
    UPDATE rms_rtt_item
    SET dt_modified = CURRENT YEAR TO fraction(3), 
        modified_by = i_modified_by
    WHERE rtt_wf_id = i_rtt_wf_id AND item_ref_no = i_item_ref_no;



    -- Get the existing rtt_item_id
    SELECT FIRST 1 rtt_item_id
    INTO o_rtt_item_id
    FROM rms_rtt_item
    WHERE rtt_wf_id = i_rtt_wf_id AND item_ref_no = i_item_ref_no;

    IF (o_rtt_item_id IS NULL OR o_rtt_item_id < 1) THEN
        LET o_rtt_item_id = -1; -- Indicate failure
    END IF;
END IF;

-- commit all the above work
COMMIT WORK;
-- Return the rtt_item_id
RETURN o_rtt_item_id;

END PROCEDURE;


-- insert fms apia id 

DROP PROCEDURE sp_insfmsapia_id;
CREATE OR REPLACE PROCEDURE "rms".sp_insfmsapia_id(
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

IF i_item_ref_no = 'NON-RMS' THEN
    LET i_item_ref_no = i_item_desc;
END IF;
 
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
    IF i_orn_no IS NULL OR TRIM(i_orn_no) = '' THEN
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
    IF i_orn_no IS NULL OR TRIM(i_orn_no) = '' THEN
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
        ELIF v_rtt_status = 'BE' THEN
            -- Bank Error: increment appeal count but no limit restriction
            -- (User error in bank details, but allow unlimited corrections)
            LET v_appeal_cnt = v_max_appeal_cnt + 1;
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
    IF i_orn_no IS NULL OR TRIM(i_orn_no) = '' THEN
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


DROP PROCEDURE sp_getRTTssdocref_id;
CREATE OR REPLACE PROCEDURE sp_getRTTssdocref_id(i_rtt_wf_id INT)
   RETURNING NVARCHAR(255) AS ssdocref_id;
   
   -- Local variables
   DEFINE v_ssdocref_id NVARCHAR(255);

   -- Handle no data found
   ON EXCEPTION IN (-100)
      RETURN NULL;
   END EXCEPTION

   SELECT t1.ssdocref_id 
     INTO v_ssdocref_id 
     FROM rms_rtt t1
     JOIN rms_rtt_wf t2 
       ON t1.rtt_app_no = t2.rtt_app_no 
    WHERE t2.rtt_wf_id = i_rtt_wf_id;
    
   RETURN v_ssdocref_id;
   
END PROCEDURE;