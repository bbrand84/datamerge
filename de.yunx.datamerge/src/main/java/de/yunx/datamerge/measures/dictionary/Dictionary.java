package de.yunx.datamerge.measures.dictionary;

import org.apache.avro.util.Utf8;

public interface Dictionary {

public int getClassification(Utf8 token);
public void addToDictionary(String token, int classification);

}
