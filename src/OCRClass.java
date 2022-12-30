/*
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OCRClass {

    private static final String apiKEY = "K86568016188957";

    public static String getStringWithOCR(File file, String fileType, String lang) {
        try {
            String encodedString = encodeFileToBase64Binary(file);
            String postRequest = "https://api.ocr.space/parse/image";
            URL obj = new URL(postRequest); // OCR API Endpoints
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            JSONObject postDataParams = new JSONObject();

            postDataParams.put("apikey", apiKEY);
            postDataParams.put("isOverlayRequired", false);

            if (lang != null) postDataParams.put("language", lang);
            if (fileType != null) postDataParams.put("filetype",fileType);

            if (fileType == null) fileType = getExtensionByApacheCommonLib(file.getAbsolutePath());

            encodedString = "data:image/" + fileType + ";base64," + encodedString;

            postDataParams.put("base64Image", encodedString);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(getPostDataString(postDataParams));
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //return result
            return getParsedText(String.valueOf(response));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getStringArrayWithOCR(File file, String fileType, String lang) {

        String fullMsg = getStringWithOCR(file, fileType, lang);
        assert fullMsg != null;
        String[] fullMsgArr = fullMsg.split(System.getProperty("line.separator"));

        return new ArrayList<>(Arrays.asList(fullMsgArr));

    }

    public static String getStringWithOCR(String imgURL, String fileType, String lang) {

        String website = "";

        String imgType = "";

        if (fileType == null) {
            String[] imgURLSplit = imgURL.split("\\.");
            imgType = imgURLSplit[imgURLSplit.length-1];
        } else {
            imgType = fileType;
        }

        String getRequest = "https://api.ocr.space/parse/imageurl?apikey=";
        if (lang == null) {
            website = (getRequest + apiKEY + "&url=" + imgURL + "&filetype=" + imgType);
        } else {
            website = (getRequest + apiKEY + "&url=" + imgURL + "&filetype=" + imgType + "&language=" + lang);
        }

        try {
            URL url = new URL(website);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            con.setReadTimeout(40000);

            BufferedReader br;
            String line;
            StringBuilder response = new StringBuilder();

            int status = con.getResponseCode();
            if (con.getResponseCode() >= 300) {

                System.out.println("There was a problem while sending a request to the API.\nPlease check your connection and try again.");
                System.out.println("Status code: " + status);
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                System.out.println("Response: " + response.toString());

                return null;
            }

            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            String result = response.toString();

            String fullMsg = getParsedText(result);

            con.disconnect();

            return fullMsg;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getStringArrayWithOCR(String imgURL, String fileType, String lang) {
        String fullMsg = getStringWithOCR(imgURL, fileType, lang);
        ArrayList<String> lines = new ArrayList<>();
        assert fullMsg != null;
        String[] fullMsgArr = fullMsg.split(System.getProperty("line.separator"));

        Collections.addAll(lines, fullMsgArr);

        return lines;
    }

    public static String getStringWithOCR(BufferedImage img, String fileType, String lang) {
        try {
            File outputfile = new File("image12312345.jpg");
            String newType = (fileType != null) ? fileType : "png";
            ImageIO.write(img, "jpg", outputfile);

            String ocrString = getStringWithOCR(outputfile, fileType, lang);
            System.out.println("Deleted?: " + outputfile.delete());
            outputfile.deleteOnExit();

            return ocrString;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getStringArrayWithOCR(BufferedImage img, String fileType, String lang) {
        String fullMsg = getStringWithOCR(img, fileType, lang);
        ArrayList<String> lines = new ArrayList<>();
        assert fullMsg != null;
        String[] fullMsgArr = fullMsg.split(System.getProperty("line.separator"));

        Collections.addAll(lines, fullMsgArr);

        return lines;
    }

    private static String getParsedText(String fullResult) {
        try {
            JSONParser jp = new JSONParser();
            JSONObject obj = (JSONObject) jp.parse(fullResult);
            JSONArray arr = (JSONArray) obj.get("ParsedResults");
            JSONObject text = (JSONObject) arr.get(0);
            return (String) text.get("ParsedText");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encodeFileToBase64Binary(File file) {
        String encodedString = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedString = new String(Base64.getEncoder().encode(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    private static String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String key : (Iterable<String>) params.keySet()) {

            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));

        }
        return result.toString();
    }

    private static String getExtensionByApacheCommonLib(String filename) {
        return FilenameUtils.getExtension(filename);
    }
}


 */