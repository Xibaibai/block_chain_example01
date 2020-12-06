package example01;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Class: MyContract
 */
@Contract(
    name = "example01.MyContract",
    info = @Info(
        title = "MyContract",
        description = "SmartContract Example 01 - Blockchain Workshop",
        version = "1.0.0",
        license = @License(
            name = "Apache 2.0 License",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
        contact = @Contact(
            email = "245086962@qq.com",
            name = "Tony T Song"
        )
    )
)
@Default
public final class MyContract implements ContractInterface {

    enum Message {
        UNKNOWN_ERROR("chaincode failed with unknown reason."),
        FUNC_NOT_SUPPORT("function name '%s' is not support."),
        ARG_NUM_WRONG("Incorrect number of arguments. (Expecting %d)"),
        ACCOUNT_NOT_EXISTING("Account '%s' does not exist."),
        NO_ENOUGH_BALANCE("There is no enough balance to transfer in account '%s'."),
        BALANCE_INVALID("Account balance is invalid. ('%s': %s)");

        private String tmpl;

        private Message(String tmpl) {
            this.tmpl = tmpl;
        }

        public String template() {
            return this.tmpl;
        }
    }


    /**
     * Initialize Ledger
     * @param ctx context
     */
    @Transaction(name = "Init", intent = Transaction.TYPE.SUBMIT)
    public void init(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState("Name", "Fabric@Java");
    }

    /**
     * Query Ledger
     * @param ctx context
     * @return name state in ledger
     */
    @Transaction(name = "Hi", intent = Transaction.TYPE.EVALUATE)
    public String hi(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        return stub.getStringState("Name");
    }

    @Transaction(name = "SetValue", intent = Transaction.TYPE.EVALUATE)
    public void setValue(final Context ctx, String key, String value) {
        ChaincodeStub stub = ctx.getStub();

        try {
            Integer.valueOf(value);
        } catch (Exception e) {
            String errorMessage = String.format(Message.BALANCE_INVALID.template(), key, value);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, e);
        }

        stub.putStringState(key,value);
    }

    @Transaction(name = "GetValue", intent = Transaction.TYPE.EVALUATE)
    public String getValue(final Context ctx, String key) {
        ChaincodeStub stub = ctx.getStub();
       return stub.getStringState(key);
    }

}

