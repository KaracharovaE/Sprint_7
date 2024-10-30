package sprint7;

public class Courier {

    private String login;
    private String password;
    private String firstName;


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Courier withLogin(String login) {
        this.login = login;
        return this;
    }

    public Courier withPassword(String password) {
        this.password = password;
        return this;
    }

    public Courier withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Courier withEmptyPassword() {
        this.password = "";
        return this;
    }
}