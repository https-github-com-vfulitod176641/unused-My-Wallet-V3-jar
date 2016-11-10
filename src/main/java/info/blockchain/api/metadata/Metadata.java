package info.blockchain.api.metadata;

import info.blockchain.api.metadata.data.Auth;
import info.blockchain.api.metadata.data.Message;
import info.blockchain.api.metadata.data.Status;
import info.blockchain.api.metadata.data.Trusted;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.util.encoders.Base64;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Potentially move this to Android
 */
public class Metadata {

    MetadataService mds;

    public Metadata() {

        //Debug
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        //Setup retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MetadataService.API_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mds = retrofit.create(MetadataService.class);
    }

    /**
     * Get nonce generated by the server (auth challenge).
     */
    public String getNonce() throws Exception {

        Call<Auth> response = mds.getNonce();

        Response<Auth> exe = response.execute();

        System.out.println(response.request().url());

        if (exe.isSuccessful()) {
            return exe.body().getNonce();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get JSON Web Token if signed nonce is correct. Signed.
     */
    public String getToken(ECKey key) throws Exception {

        String mdid = key.toAddress(MainNetParams.get()).toString();
        String nonce = getNonce();
        String sig = key.signMessage(nonce);

        HashMap<String, String> map = new HashMap<>();
        map.put("mdid", mdid);
        map.put("signature", sig);
        map.put("nonce", nonce);
        Call<Auth> response = mds.getToken(map);

        Response<Auth> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getToken();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get list of all trusted MDIDs. Authenticated.
     */
    public Trusted getTrustedList(String token) throws Exception {

        Call<Trusted> response = mds.getTrustedList("Bearer " + token);
//        System.out.println("curl -X GET http://localhost:8080/trusted -H \"Authorization: Bearer "+token+"\"");

        Response<Trusted> exe = response.execute();

        System.out.println(exe.body().toString());

        if (exe.isSuccessful()) {
            return exe.body();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Check if a contact is on trusted list of mdid. Authenticated.
     */
    public boolean getTrusted(String token, String mdid) throws Exception {

        Call<Trusted> response = mds.getTrusted("Bearer " + token, mdid);
//        System.out.println("curl -X GET http://localhost:8080/trusted?mdid="+mdid+" -H \"Authorization: Bearer "+token+"\"");
        Response<Trusted> exe = response.execute();

        if (exe.isSuccessful()) {
            return Arrays.asList(exe.body().getContacts()).contains(mdid);
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Add a contact to trusted list of mdid. Authenticated.
     */
    public boolean putTrusted(String token, String mdid) throws Exception {

        Call<Trusted> response = mds.putTrusted("Bearer " + token, mdid);
//        System.out.println("curl -X PUT http://localhost:8080/trusted/"+mdid+" -H \"Authorization: Bearer "+token+"\"");
        Response<Trusted> exe = response.execute();

        if (exe.isSuccessful()) {
            return mdid.equals(exe.body().getContact());
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Delete a contact from trusted list of mdid. Authenticated.
     */
    public boolean deleteTrusted(String token, String mdid) throws Exception {

        Call<Status> response = mds.deleteTrusted("Bearer " + token, mdid);
//        System.out.println("curl -X DELETE http://localhost:8080/trusted/"+mdid+" -H \"Authorization: Bearer "+token+"\"");
        Response<Status> exe = response.execute();

        if (exe.isSuccessful()) {
            return true;
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Add new shared metadata entry. Signed. Authenticated.
     */
    public Message postMessage(String token, ECKey key, String recipientMdid, String message, int type) throws Exception {

        String b64Msg = new String(Base64.encode(message.getBytes()));

        String signature = key.signMessage(b64Msg);

        Message request = new Message();
        request.setRecipient(recipientMdid);
        request.setType(type);
        request.setPayload(b64Msg);
        request.setSignature(signature);

        Call<Message> response = mds.postMessage("Bearer " + token, request);
//        System.out.println("curl -X POST http://localhost:8080/messages -H \"Content-Type: application/json\" -H \"Authorization: Bearer "+token+"\" -d '"+new Gson().toJson(request)+"'");
        Response<Message> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body();
        } else {
            throw new Exception(exe.message());
        }

    }

    /**
     * Get messages sent to my MDID. Authenticated.
     */
    public List<Message> getMessages(String token, boolean onlyProcessed) throws Exception {

        Call<List<Message>> response = mds.getMessages("Bearer " + token, onlyProcessed);

        Response<List<Message>> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get messages sent to my MDID. Authenticated.
     */
    public List<Message> getMessages(String token, String lastMessageId) throws Exception {

        Call<List<Message>> response = mds.getMessages("Bearer " + token, lastMessageId);
//        System.out.println("curl -X GET http://localhost:8080/messages?id="+lastMessageId+" -H \"Authorization: Bearer "+token+"\"");
        Response<List<Message>> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get messages sent to my MDID. Authenticated.
     */
    public Message getMessage(String token, String messageId) throws Exception {

        Call<Message> response = mds.getMessage("Bearer " + token, messageId);

        System.out.println(response.request().url());

        Response<Message> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body();
        } else {
            throw new Exception(exe.message());
        }
    }

    public boolean processMessage(String token, String messageId, boolean processed) throws Exception {

        Call<Message> response = mds.processMessage("Bearer " + token, messageId, processed);
//        System.out.println("curl -X GET http://localhost:8080/messages?id="+lastMessageId+" -H \"Authorization: Bearer "+token+"\"");

        System.out.println("Bearer " + token);

        System.out.println(response.request().url());

        Response<Message> exe = response.execute();

        if (exe.isSuccessful()) {
            return true;
        } else {
            throw new Exception(exe.message());
        }
    }
}