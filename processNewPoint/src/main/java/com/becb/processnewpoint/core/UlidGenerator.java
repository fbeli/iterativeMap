package com.becb.processnewpoint.core;

import com.github.f4b6a3.ulid.Ulid;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;


public class UlidGenerator  implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return Ulid.fast().toString();
    }
}