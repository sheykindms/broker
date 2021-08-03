package com.sheykindms.broker.inmemorystore;

import com.sheykindms.broker.model.WatchList;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {
  private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();

  public WatchList getWatchList(UUID accountId) {
    return watchListsPerAccount.getOrDefault(accountId, new WatchList());
  }

  public WatchList updateWatchList(UUID accountId, WatchList watchList) {
    watchListsPerAccount.put(accountId, watchList);
    return watchList;
  }

  public void deleteWatchList(UUID accountId) {
    watchListsPerAccount.remove(accountId);
  }
}
