package transfer.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RestClient {
    private Client client;

    public RestClient() {
        client = Client.create();
    }

    public static void main(String[] args) {
        String command = args[0];
        RestClient rest = new RestClient();
        if (command.equals("getacc")) {
            String acc = args[1];
            if (checkFormat(acc))
                rest.getAccInfo(acc);
            else
                System.err.println("Account have bad format");
        } else if (command.equals("getaccs")) {
            rest.getAccInfoAll();
        } else if (command.equals("transfer")) {
            if (!checkFormat(args[1])) {
                System.err.println("Account {From} have bad format");
                return;
            }
            if (!checkFormat(args[2])) {
                System.err.println("Account {To} have bad format");
                return;
            }
            Pattern p = Pattern.compile("^[0-9]{1,}$");
            Matcher m = p.matcher(args[3]);
            if (!m.matches()) {
                System.err.println("Sum incorrect");
                return;
            }
            rest.transferMoney(args[1], args[2], args[3]);
        } else {
            System.err.println("unknown command");
        }
    }

    private static boolean checkFormat(String str) {
        Pattern p = Pattern.compile("^[0-9]{20}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    private void getAccInfo(String acc) {
        WebResource webResource = client
                .resource("http://localhost:8084/rest/account/getAcc/" + acc);

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        printResult(response);
    }

    private void getAccInfoAll() {
        WebResource webResource = client
                .resource("http://localhost:8084/rest/account/getAccs/");

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        printResult(response);
    }

    private void transferMoney(String from, String to, String sum) {
        String request = "from=" + from + "&to=" + to +
                "&sum=" + sum;

        WebResource webResource = client
                .resource("http://localhost:8084/rest/account/transfer");

        ClientResponse response = webResource.accept("application/json")
                .type("application/json").put(ClientResponse.class, request);

        printResult(response);
    }

    private void printResult(ClientResponse response) {
        String output = response.getEntity(String.class);
        int code = response.getStatus();

        System.out.println("Output from Server .... \n");
        if (code != 200)
            System.out.println("Failed : HTTP error code : " + code);
        System.out.println(output);
    }
}
