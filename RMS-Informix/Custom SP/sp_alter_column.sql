CREATE OR REPLACE PROCEDURE sp_alter_column(
    i_table_nm NVARCHAR(50), 
    i_column_nm NVARCHAR(50),
    i_data_type NVARCHAR(50)
) RETURNING NVARCHAR(50);

    DEFINE v_result NVARCHAR(50);
    DEFINE v_table_exists INTEGER;
    DEFINE v_column_exists INTEGER;
    
    -- Check if column exists
    SELECT COUNT(1) INTO v_table_exists 
    FROM systables 
    WHERE tabname = i_table_nm;
    
    -- Check if column exists
    SELECT COUNT(1) INTO v_column_exists 
    FROM syscolumns 
    WHERE tabid = (SELECT tabid FROM systables WHERE tabname = i_table_nm) 
    AND colname = i_column_nm;
    
    -- Modify or add the column based on existence
    IF v_table_exists > 0 AND v_column_exists > 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ' || i_table_nm || ' MODIFY (' || i_column_nm || ' ' || i_data_type || ')';
        LET v_result = 'Modified '|| i_table_nm || '.' || i_column_nm || ' [ ' || i_data_type || ' ]';
    ELIF v_table_exists > 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ' || i_table_nm || ' ADD (' || i_column_nm || ' ' || i_data_type || ')';
        LET v_result = 'Added '|| i_table_nm || '.' || i_column_nm || ' [ ' || i_data_type || ' ]';
    ELSE
        LET v_result = 'Table Not Exists: '|| i_table_nm;
    END IF;

    RETURN v_result;

END PROCEDURE;