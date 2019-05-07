import com.dani.vbank.model.Transaction;
import com.dani.vbank.model.Transactions;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.FileReader;

public class TransactionsXMLTest {

    @Test
    public void testReadXML() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Transactions.class, Transaction.class);
        Transactions transactions = (Transactions) context.createUnmarshaller()
                .unmarshal(this.getClass().getResourceAsStream("/transactions.xml"));
        Assert.assertEquals(2, transactions.getTransactions().size());
    }
}
