ALTER TABLE rms_fms_apia_v ADD description1 VARCHAR(255);
GO
ALTER TABLE rms_fms_apia_v ADD paymentinstructionsid1 NVARCHAR(10);
GO
ALTER TABLE rms_fms_apia_v ADD paymentmethod1 NVARCHAR(50);
GO
ALTER TABLE rms_fms_apia_v ADD value1 NVARCHAR(255);
GO
ALTER TABLE rms_fms_apia_v ADD description2 NVARCHAR(255);
GO
ALTER TABLE rms_fms_apia_v ADD paymentinstructionsid2 NVARCHAR(10);
GO
ALTER TABLE rms_fms_apia_v ADD paymentmethod2 NVARCHAR(50);
GO
ALTER TABLE rms_fms_apia_v ADD value2 NVARCHAR(255);
GO
ALTER TABLE rms_fms_apia_v ADD description3 NVARCHAR(255);
GO
ALTER TABLE rms_fms_apia_v ADD paymentinstructionsid3 NVARCHAR(10);
GO
ALTER TABLE rms_fms_apia_v ADD paymentmethod3 NVARCHAR(50);
GO
ALTER TABLE rms_fms_apia_v ADD value3 NVARCHAR(255);
GO
ALTER TABLE rms_fms_acct MODIFY (acct_cd NVARCHAR(30))
GO
