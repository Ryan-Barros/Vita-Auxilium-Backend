package com.vitaauxilium.vitaauxilium.config;

import com.fasterxml.uuid.Generators;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UuidV7Generator implements IdentifierGenerator {
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return Generators.timeBasedGenerator().generate();
    }
}
