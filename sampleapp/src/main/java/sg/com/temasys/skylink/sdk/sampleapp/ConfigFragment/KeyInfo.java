package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;

/**
 * Created by Henry on 16/8/2.
 */

public class KeyInfo {

    private String key;
    private String secret;
    private String description;
    private boolean MCU;


    public KeyInfo() {
    }

    public KeyInfo(String key, String secret, String description, boolean MCU) {

        this.key = key;
        this.secret = secret;
        this.description = description;
        this.MCU = MCU;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isMCU() {
        return MCU;
    }

    public void setMCU(boolean MCU) {
        this.MCU = MCU;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "{" +
                "'key':'" + key + '\'' +
                ", 'secret':'" + secret + '\'' +
                ", 'description':'" + description + '\'' +
                ", 'MCU':" + MCU +
                '}';
    }
}
