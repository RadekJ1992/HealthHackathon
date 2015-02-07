package healthhackathon.heathhackathon;

/**
 * Created by radoslawjarzynka on 07.02.15.
 */
public class User {
    private String deviceId;
    private String latitude;
    private String longitude;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String device_id) {
        this.deviceId = device_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
