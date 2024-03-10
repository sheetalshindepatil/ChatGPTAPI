import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AccessingChatGPTApi {

    public static void main(String[] args) throws Exception {
        String apiKey = "";
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful assistant."));
        //messages.add(new Message("user", "Who won the world series in 2020?"));

        String response = makeApiRequest(apiKey, messages);

        // Handle the response as needed
        System.out.println(response);
    }

    private static String makeApiRequest(String apiKey, List<Message> messages) throws Exception {
        String endpoint = "https://api.openai.com/v1/chat/completions";
        String data = buildRequestData(messages);

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
        	System.out.println(data);
            outputStream.writeBytes(data);
            outputStream.flush();
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                return response.toString();
            }
        } else {
            throw new RuntimeException("API request failed with response code: " + responseCode);
        }
    }

    private static String buildRequestData(List<Message> messages) {
    	 StringBuilder jsonData = new StringBuilder("{ \"model\": \"gpt-3.5-turbo\", \"messages\": [");

         for (Message message : messages) {
             jsonData.append("{ \"role\": \"").append(message.getRole()).append("\", \"content\": \"")
                     .append(URLEncoder.encode(message.getContent(), java.nio.charset.StandardCharsets.UTF_8)).append("\" },");
         }

         jsonData.deleteCharAt(jsonData.length() - 1); // Remove the trailing comma
         jsonData.append("] }");

         return jsonData.toString();
    }

    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
