DROP PROCEDURE sp_updaterefundstatus;
CREATE OR REPLACE PROCEDURE sp_updaterefundstatus(
    i_rtt_app_no NVARCHAR(255), 
    i_reject_reason NVARCHAR(255) 
)
RETURNING NVARCHAR(255) AS rtt_status,
          NVARCHAR(255) AS refund_ty; -- Returns rtt_status and refund_ty
    DEFINE rtt_status NVARCHAR(255);
    DEFINE v_refund_ty NVARCHAR(255);
    DEFINE o_row_count INT;
    DEFINE v_rtt_app_no NVARCHAR(255);
    DEFINE v_rtt_wf_id INT;
    DEFINE v_rtt_wf_hist_id INT;
    DEFINE v_orn_no NVARCHAR(255);
    DEFINE o_result int8;
    DEFINE o_insresult int8;
    
    SELECT rtt_app_no 
    INTO v_rtt_app_no
    FROM rms_rtt
    WHERE rtt_app_no = i_rtt_app_no;
    
    IF v_rtt_app_no = i_rtt_app_no THEN
        UPDATE rms_rtt
        SET 
            rtt_status = 'BE'
        WHERE 
            rtt_app_no = i_rtt_app_no;
        LET o_row_count = DBINFO('sqlca.sqlerrd2');
        
        UPDATE rms_rtt_wf
        SET 
            rtt_status = 'BE'
        WHERE 
            rtt_app_no = i_rtt_app_no;
        LET o_row_count = DBINFO('sqlca.sqlerrd2');
        
        LET rtt_status = 'BE';
        
        --- Get rtt_wf variables including refund_ty
        SELECT rtt_wf_id, orn_no, refund_ty 
        INTO v_rtt_wf_id, v_orn_no, v_refund_ty
        FROM rms_rtt_wf 
        WHERE rtt_app_no = i_rtt_app_no;
        
        --- insert into rtt_wf_hist
        INSERT INTO rms_rtt_wf_hist 
            (rtt_wf_id, orn_no, created_by, modified_by, dt_created, dt_modified, action, dt_action, rtt_status) 
        VALUES 
            (v_rtt_wf_id, v_orn_no, 'system', 'system', CURRENT, CURRENT, 'Bank Error', CURRENT, 'BE');
        LET o_result = dbinfo('serial8');
        LET v_rtt_wf_hist_id = DBINFO('sqlca.sqlerrd1');
        
        EXECUTE PROCEDURE sp_insrttwfmsg(v_rtt_wf_id, v_rtt_wf_hist_id, current, i_reject_reason, 'system') INTO o_insresult;    
    ELSE
        LET o_row_count = 0;
        LET rtt_status = 'NA';
        LET v_refund_ty = 'NA'; -- Set default value when record not found
    END IF;
    
    RETURN rtt_status, v_refund_ty;
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