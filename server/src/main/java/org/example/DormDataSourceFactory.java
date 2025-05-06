package org.example;

import java.io.IOException;

// DormDataSourceFactory.java
public class DormDataSourceFactory {
  public enum DataSourceType {
    MOCK,
    FIREBASE
  }

  public static DormDataSource createDataSource(DataSourceType type) throws IOException {
    switch (type) {
      case MOCK:
        return new MockDormDataSource();
      case FIREBASE:
        // This will be implemented later
        return new FirebaseDormDatasource();
//        throw new UnsupportedOperationException("Firebase implementation not ready yet");
      default:
        throw new IllegalArgumentException("Unknown data source type");
    }
  }
}