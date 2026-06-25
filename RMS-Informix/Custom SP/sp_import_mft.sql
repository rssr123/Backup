CREATE OR REPLACE PROCEDURE sp_import_mft (
    i_fee_detail_id NVARCHAR(10),
    i_ss_fee_grp_id INTEGER, 
    i_fee_grp_nm_en NVARCHAR(50), 
    i_fee_detail_nm_en NVARCHAR(100),
    i_fee_detail_nm_bm NVARCHAR(100),
    i_unit_fee DECIMAL, 
    i_promo_startdt DATE, 
    i_promo_enddt DATE, 
    i_promo_fee DECIMAL,
    i_tax_cd NVARCHAR(10),
    i_allow_otc NVARCHAR(3),
    i_ll_parent_id NVARCHAR(10), 
    i_ll_start_day INTEGER, 
    i_ll_start_mth INTEGER, 
    i_ll_end_day INTEGER, 
    i_ll_end_mth INTEGER,
    i_ledger_cd NVARCHAR(50), 
    i_ss_cd NVARCHAR(100)
) RETURNING INTEGER;

    DEFINE v_ss_exists INTEGER;
    DEFINE v_fee_grp_exists INTEGER;
    DEFINE v_fee_grp_id INTEGER;
    DEFINE v_tax_cd_exists INTEGER;
    DEFINE v_tax_cd_id INTEGER;
    DEFINE v_allow_otc INTEGER;
    DEFINE row_count INTEGER;
    DEFINE v_mft_exists INTEGER;

    -- Check if ss exists
    SELECT COUNT(1) INTO v_ss_exists
    FROM rms_ss
    WHERE 1=1
    AND ss_cd = i_ss_cd
    AND status = 'A';

    IF v_ss_exists <= 0 THEN
        INSERT INTO rms_ss (
            ss_cd, ss_nm
            , dt_created, dt_modified, created_by, modified_by, status
        ) VALUES (
            i_ss_cd, i_ss_cd
            , CURRENT, CURRENT, 'system', 'system', 'A'
        );        
    END IF;

    -- Check if fee group exists
    SELECT COUNT(1) INTO v_fee_grp_exists
    FROM rms_fee_group
    WHERE 1=1
    AND ss_fee_grp_id = i_ss_fee_grp_id
    AND ss_cd = i_ss_cd
    AND status = 'A';

    IF v_fee_grp_exists <= 0 THEN
        INSERT INTO rms_fee_group (
            fee_grp_nm_en, fee_grp_nm_bm, dt_created, dt_modified, 
            created_by, modified_by, status, ss_fee_grp_id, ss_cd
        ) VALUES (
            i_fee_grp_nm_en, i_fee_grp_nm_en, CURRENT, CURRENT, 
            'system', 'system', 'A', i_ss_fee_grp_id, i_ss_cd
        );

        LET v_fee_grp_id = DBINFO('Serial8');
    ELSE
        
        SELECT fee_grp_id INTO v_fee_grp_id
        FROM rms_fee_group
        WHERE 1=1
        AND ss_fee_grp_id = i_ss_fee_grp_id
        AND ss_cd = i_ss_cd
        AND status = 'A';

        UPDATE rms_fee_group SET
        fee_grp_nm_en = i_fee_grp_nm_en
        , fee_grp_nm_bm = i_fee_grp_nm_en
        , modified_by = 'system'
        , dt_modified = CURRENT
        where fee_grp_id = v_fee_grp_id;
        
    END IF;

    -- Check if tax code exists
    SELECT COUNT(1) INTO v_tax_cd_exists
    FROM rms_tax_code
    WHERE tax_cd = i_tax_cd
    AND status = 'A';

    IF v_tax_cd_exists <= 0 THEN
        
        SELECT tax_cd_id INTO v_tax_cd_id
        FROM rms_tax_code
        WHERE tax_cd = i_tax_cd
        AND status = 'A'
        LIMIT 1;
    ELSE
        SELECT tax_cd_id INTO v_tax_cd_id
        FROM rms_tax_code
        WHERE tax_cd = i_tax_cd
        AND status = 'A';     
    END IF;

    IF i_allow_otc = 'No' THEN 
        LET v_allow_otc = 0;
    ELSE 
        LET v_allow_otc = 1;
    END IF;
    
//    DEFINE v_parent_exists INTEGER;
//    DEFINE v_parent_length INTEGER;
//    LET v_parent_length = LENGTH(i_ll_parent_id);
//    
//    IF v_parent_length > 0 THEN
//        LET v_parent_exists = 0;
//
//        SELECT COUNT(1) INTO v_parent_exists
//        FROM rms_mft
//        WHERE fee_detail_id = i_ll_parent_id
//        --AND ss_cd = i_ss_cd
//        AND status = 'A';
//
//        IF v_parent_exists <= 0
//            LET i_ll_parent_id = '!F' || i_ll_parent_id; 
//        END IF;
//    END IF;

    -- Check if MFT exists
    SELECT COUNT(1) INTO v_mft_exists
    FROM rms_mft
    WHERE fee_detail_id = i_fee_detail_id
    --AND ss_cd = i_ss_cd
    AND status = 'A';

    IF v_mft_exists <= 0 THEN
        INSERT INTO rms_mft (
            fee_detail_id, fee_grp_id, fee_detail_nm_e, fee_detail_nm_b
            ,unit_fee, promo_startdt, promo_enddt, promo_fee, tax_cd_id, allow_otc
            ,ll_parent_id, ll_start_day, ll_start_mth, ll_end_day, ll_end_mth
            ,ledger_cd, ss_cd, dt_created, dt_modified, created_by, modified_by, status
            ,prog, is_pub
        ) VALUES (
            i_fee_detail_id, v_fee_grp_id, i_fee_detail_nm_en, i_fee_detail_nm_bm
            ,i_unit_fee, i_promo_startdt, i_promo_enddt, i_promo_fee, v_tax_cd_id, v_allow_otc
            ,i_ll_parent_id, i_ll_start_day, i_ll_start_mth, i_ll_end_day, i_ll_end_mth
            ,i_ledger_cd, i_ss_cd, CURRENT, CURRENT, 'system', 'system', 'A'
            ,null, 0
        );
    ELSE
        UPDATE rms_mft SET
            fee_grp_id = v_fee_grp_id,
            fee_detail_nm_e = i_fee_detail_nm_en,
            fee_detail_nm_b = i_fee_detail_nm_bm,
            unit_fee = i_unit_fee,
            promo_startdt = i_promo_startdt,
            promo_enddt = i_promo_enddt,
            promo_fee = i_promo_fee,
            tax_cd_id = v_tax_cd_id,
            allow_otc = v_allow_otc,
            ll_parent_id = i_ll_parent_id,
            ll_start_day = i_ll_start_day,
            ll_start_mth = i_ll_start_mth,
            ll_end_day = i_ll_end_day,
            ll_end_mth = i_ll_end_mth,
            ledger_cd = i_ledger_cd,
            ss_cd = i_ss_cd,
            dt_modified = CURRENT,
            modified_by = 'system',
            prog = NULL,
            is_pub = 0
        WHERE fee_detail_id = i_fee_detail_id
        AND status = 'A';
    END IF;

    LET row_count = DBINFO('sqlca.sqlerrd2');
    RETURN row_count;

END PROCEDURE;