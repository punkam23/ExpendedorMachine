package expendedor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import expendedor.modelo.Compra;
import expendedor.modelo.ExpendedorConfig;
import expendedor.util.request.CompraDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestConnection {
    private URL url;
    private HttpURLConnection conn;
    private BufferedReader br;

    public RestConnection() {
    }

    public void sendCompraRequest(Compra compra, ExpendedorConfig expendedorConfig){
        try {
            url = new URL("http://localhost:8080/expendedor/agregarFactura");
            CompraDTO compraDTO = new CompraDTO(compra);
            compraDTO.setExpendedorId(expendedorConfig.getId());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            ObjectMapper objectMapper = new ObjectMapper();
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = objectMapper.writeValueAsString(compraDTO)
                        .getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
