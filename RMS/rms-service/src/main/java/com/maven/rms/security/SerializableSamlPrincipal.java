package com.maven.rms.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;

import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

public class SerializableSamlPrincipal extends DefaultSaml2AuthenticatedPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    public SerializableSamlPrincipal(String name, Map<String, List<Object>> attributes) {
        super(name, attributes);
    }

    /**
     * Ensure everything in the attributes map is serializable.
     * Non-serializable attribute values are converted to Strings via Objects.toString().
     */
    private static Map<String, List<Object>> sanitizeAttributes(Map<String, List<Object>> attrs) {
        Map<String, List<Object>> out = new HashMap<>(attrs.size());
        for (Map.Entry<String, List<Object>> e : attrs.entrySet()) {
            List<Object> inList = e.getValue();
            if (inList == null) {
                out.put(e.getKey(), Collections.emptyList());
                continue;
            }
            List<Object> outList = new ArrayList<>(inList.size());
            for (Object o : inList) {
                if (o instanceof Serializable) {
                    outList.add(o);
                } else {
                    // Convert to string fallback (keeps something useful but serializable)
                    outList.add(Objects.toString(o, null));
                }
            }
            out.put(e.getKey(), Collections.unmodifiableList(outList));
        }
        return Collections.unmodifiableMap(out);
    }

    // Optional: if you want to store registrationId safely
    public void setRegistrationId(String registrationId) {
        super.setRelyingPartyRegistrationId(registrationId);
    }

    public String getRegistrationId() {
        return super.getRelyingPartyRegistrationId();
    }
}
