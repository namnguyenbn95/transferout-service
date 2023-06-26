package vn.vnpay.dbinterface.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UseExistingIdOtherwiseGenerateNewOne extends SequenceStyleGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable ser = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
        if (ser != null) {
            return ser;
        }
        try {
            Connection connection = session.connection();
            PreparedStatement ps = connection.prepareStatement("SELECT sme_trans_seq.nextval FROM dual");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("nextval");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
