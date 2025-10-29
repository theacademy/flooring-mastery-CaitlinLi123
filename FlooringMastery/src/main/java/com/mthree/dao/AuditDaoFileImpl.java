package com.mthree.dao;

import com.mthree.exception.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class AuditDaoFileImpl implements AuditDao{
    private final static String AUDIT_FILE = "Data/audit.txt";


    @Override
    public void writeAuditEntry(String entry) throws PersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(AUDIT_FILE,true));
        }catch (IOException e){
            throw new PersistenceException("Could not persist audit information.", e);
        }

        LocalDateTime timestamp = LocalDateTime.now();
        out.println(timestamp.toString()+": "+entry);
        out.flush();
    }
}
