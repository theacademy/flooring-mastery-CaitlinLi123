package com.mthree.dao;

import com.mthree.exception.PersistenceException;

public interface AuditDao {
    void writeAuditEntry(String auditMsg) throws PersistenceException;
}
