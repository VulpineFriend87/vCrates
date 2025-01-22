package pro.vulpine.vCrates.instance;

import java.util.List;

public class CrateKeys {

    private final boolean required;
    private final List<String> allowedKeys;

    public CrateKeys(boolean required, List<String> allowedKeys) {
        this.required = required;
        this.allowedKeys = allowedKeys;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getAllowedKeys() {
        return allowedKeys;
    }

    public boolean isKeyAllowed(String identifier) {
        return allowedKeys.contains(identifier);
    }

}
