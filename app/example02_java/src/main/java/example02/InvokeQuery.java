package example02;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InvokeQuery {
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private static final String ORGNAME_ORG1 = "Org1";
    private static final String ORGNAME_ORG2 = "Org2";
    private static final String USERNAME_ORG1 = "user01";
    private static final String USERNAME_ORG2 = "user02";
    private static final String CHANNEL_NAME = "mychannel";
    private static final String CONTRACT_NAME = "mycc";

    private static void doQuery(String orgName, String userName, String functionName, String key)
            throws IOException, ContractException {
        Path walletPath = Paths.get("wallet", orgName);
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

        Identity identity = wallet.get(userName);

        if (identity == null) {
            System.out.println("The identity \"" + userName + "@"+ orgName + "\" doesn't exists in the wallet");
            return;
        }

        Path networkConfigPath = Paths.get( "profiles", orgName, "connection.json");
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, userName).networkConfig(networkConfigPath).discovery(true);

        // create a gateway connection
        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CONTRACT_NAME);

            byte[] result = contract.evaluateTransaction(functionName, key);
            System.out.println(new String(result));
        }
    }

    public static void main(String[] args) {
        try {
            doQuery(ORGNAME_ORG1, USERNAME_ORG1, "query", "a");
            doQuery(ORGNAME_ORG2, USERNAME_ORG2, "query", "b");
        } catch (IOException | ContractException e) {
            e.printStackTrace();
        }
    }
}
