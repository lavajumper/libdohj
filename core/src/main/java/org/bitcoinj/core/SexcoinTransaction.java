package org.bitcoinj.core;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SexcoinTransaction extends Transaction {


    public SexcoinTransaction(NetworkParameters params) {
        super(params);
    }

    public SexcoinTransaction(NetworkParameters params, byte[] payloadBytes) throws ProtocolException {
        super(params, payloadBytes);
    }

    public SexcoinTransaction(NetworkParameters params, byte[] payload, int offset) throws ProtocolException {
        super(params, payload, offset);
    }

    public SexcoinTransaction(NetworkParameters params, byte[] payload, int offset, @Nullable Message parent, MessageSerializer setSerializer, int length) throws ProtocolException {
        super(params, payload, offset, parent, setSerializer, length);
    }

    public SexcoinTransaction(NetworkParameters params, byte[] payload, @Nullable Message parent, MessageSerializer setSerializer, int length) throws ProtocolException {
        super(params, payload, parent, setSerializer, length);
    }

    public void populateBlockAppearance(Map<Sha256Hash,Integer> appearences){
        Map<Sha256Hash, Integer> buff = new TreeMap<Sha256Hash, Integer>();
        buff.putAll(appearences);
        Iterator<Map.Entry<Sha256Hash, Integer>> iterator = buff.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = iterator.next();
            addBlockAppearance((Sha256Hash)entry.getKey(), (int)entry.getValue());
        }
    }

    public boolean isOverConsent(){
        return ((getVersion() & TransactionFlags.TX_F_IS_OVER_CONSENT) == TransactionFlags.TX_F_IS_OVER_CONSENT);
    }

    public boolean isOver18(){
        return ((getVersion() & TransactionFlags.TX_F_IS_OVER_18) == TransactionFlags.TX_F_IS_OVER_18);

    }

    public boolean isOver21(){
        return ((getVersion() & TransactionFlags.TX_F_IS_OVER_21) == TransactionFlags.TX_F_IS_OVER_21);
    }

    public boolean isOverNone(){
        if(getVersion() < 65537)
            return true;
        return false;
    }

    public int getTransactionFlags(){
        return( (int)(getVersion() >> 16 ) );
    }
}
