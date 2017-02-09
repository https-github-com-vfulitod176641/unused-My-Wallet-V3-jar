package info.blockchain.wallet.contacts.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.core.Coin;
import org.bitcoinj.uri.BitcoinURI;

import java.io.IOException;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilitatedTransaction {

    public static final String STATE_WAITING_FOR_ADDRESS = "waiting_address";
    public static final String STATE_WAITING_FOR_PAYMENT = "waiting_payment";
    public static final String STATE_PAYMENT_BROADCASTED = "payment_broadcasted";

    public static final String ROLE_RPR_INITIATOR = "rpr_initiator";
    public static final String ROLE_RPR_RECEIVER = "rpr_receiver";
    public static final String ROLE_PR_INITIATOR = "pr_initiator";
    public static final String ROLE_PR_RECEIVER = "pr_receiver";

    private String id;
    private String state;
    private long intendedAmount;
    private String address;
    private String txHash;
    private String role;
    private long created;
    private String note;

    public FacilitatedTransaction() {
        this.id = UUID.randomUUID().toString();
        this.created = System.currentTimeMillis();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("intended_amount")
    public long getIntendedAmount() {
        return intendedAmount;
    }

    @JsonProperty("intended_amount")
    public void setIntendedAmount(long intendedAmount) {
        this.intendedAmount = intendedAmount;
    }

    @JsonProperty("tx_hash")
    public String getTxHash() {
        return txHash;
    }

    @JsonProperty("tx_hash")
    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreated() {
        return created;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonIgnore
    public String toBitcoinURI() {
        return BitcoinURI.convertToBitcoinURI(address, Coin.valueOf(intendedAmount), null, null);
    }

    @JsonIgnore
    public FacilitatedTransaction fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, FacilitatedTransaction.class);
    }

    @JsonIgnore
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
