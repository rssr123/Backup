package com.maven.rms.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

import org.springframework.security.saml2.Saml2Exception;

public final class Saml2Utils {

    private Saml2Utils() {}

    public static String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    public static String base64Encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String inflate(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        try (
            ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
            InflaterInputStream iis = new InflaterInputStream(bais, new Inflater(true));
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new RuntimeException("Failed to inflate SAMLRequest", e);
        }
    }

    public static String deflateAndBase64(String xml) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater(Deflater.DEFLATED, true))
        ) {
            dos.write(xml.getBytes(StandardCharsets.UTF_8));
            dos.finish();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to deflate SAMLRequest", e);
        }
    }

    /**
     * Naive XML insertion of ForceAuthn="true".
     * Replace with proper OpenSAML marshalling if needed.
     */
    public static String addForceAuthn(String xml) {
        if (xml.contains("ForceAuthn=")) {
            return xml; // already set
        }
        return xml.replaceFirst("<saml2p:AuthnRequest",
                "<saml2p:AuthnRequest ForceAuthn=\"true\"");
    }
    
    public static String samlEncode(byte[] b) {
		return Base64.getEncoder().encodeToString(b);
	}

    public static byte[] samlDecode(String s) {
		return Base64.getMimeDecoder().decode(s);
	}

	static byte[] samlDeflate(String s) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DeflaterOutputStream deflater = new DeflaterOutputStream(b, new Deflater(Deflater.DEFLATED, true));
			deflater.write(s.getBytes(StandardCharsets.UTF_8));
			deflater.finish();
			return b.toByteArray();
		}
		catch (IOException ex) {
			throw new Saml2Exception("Unable to deflate string", ex);
		}
	}

	public static String samlInflate(byte[] b) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InflaterOutputStream iout = new InflaterOutputStream(out, new Inflater(true));
			iout.write(b);
			iout.finish();
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		}
		catch (IOException ex) {
			throw new Saml2Exception("Unable to inflate string", ex);
		}
	}
}