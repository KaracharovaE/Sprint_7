package sprint7;

public class OrderRequest {
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime; // должен быть инт
    private final String deliveryDate;
    private final String comment;
    private final String[] color;

    public OrderRequest(String firstName, String lastName, String address, String metroStation,
                        String phone, int rentTime, String deliveryDate, String comment, String color) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color != null ? color.split(", ") : new String[0];
    }
}
