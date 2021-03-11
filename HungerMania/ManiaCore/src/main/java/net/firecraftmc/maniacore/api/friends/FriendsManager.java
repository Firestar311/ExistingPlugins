package net.firecraftmc.maniacore.api.friends;

import net.firecraftmc.maniacore.api.records.FriendshipRecord;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.util.Pair;

import java.util.*;

public class FriendsManager {
    
    public void loadDataFromDatabase(UUID player) {
        Database database = ManiaCore.getInstance().getDatabase();
        List<IRecord> friendshipRecords = new ArrayList<>(database.getRecords(net.firecraftmc.maniacore.api.records.FriendshipRecord.class, "player1", player.toString()));
        friendshipRecords.addAll(database.getRecords(net.firecraftmc.maniacore.api.records.FriendshipRecord.class, "player2", player.toString()));
        
        List<IRecord> friendRequestRecords = new ArrayList<>(database.getRecords(net.firecraftmc.maniacore.api.records.FriendRequestRecord.class, "from", player.toString()));
        friendRequestRecords.addAll(database.getRecords(net.firecraftmc.maniacore.api.records.FriendshipRecord.class, "to", player.toString()));
        
        List<IRecord> friendNotificationRecords = new ArrayList<>(database.getRecords(net.firecraftmc.maniacore.api.records.FriendNotificationRecord.class, "sender", player.toString()));
        friendNotificationRecords.addAll(database.getRecords(net.firecraftmc.maniacore.api.records.FriendshipRecord.class, "target", player.toString()));
        
        for (IRecord record : friendshipRecords) {
            if (record instanceof net.firecraftmc.maniacore.api.records.FriendshipRecord) {
                net.firecraftmc.maniacore.api.records.FriendshipRecord friendshipRecord = (net.firecraftmc.maniacore.api.records.FriendshipRecord) record;
                Redis.pushFriendship(friendshipRecord.toObject());
            }
        }
        
        for (IRecord record : friendRequestRecords) {
            if (record instanceof net.firecraftmc.maniacore.api.records.FriendRequestRecord) {
                net.firecraftmc.maniacore.api.records.FriendRequestRecord friendshipRecord = (net.firecraftmc.maniacore.api.records.FriendRequestRecord) record;
                Redis.pushFriendRequest(friendshipRecord.toObject());
            }
        }
        
        for (IRecord record : friendNotificationRecords) {
            if (record instanceof net.firecraftmc.maniacore.api.records.FriendNotificationRecord) {
                net.firecraftmc.maniacore.api.records.FriendNotificationRecord friendshipRecord = (net.firecraftmc.maniacore.api.records.FriendNotificationRecord) record;
                Redis.pushFriendNotification(friendshipRecord.toObject());
            }
        }
    }
    
    public FriendResult addRequest(User sender, User target) {
        if (getFriendship(sender.getUniqueId(), target.getUniqueId()) != null) {
            return FriendResult.ALREADY_FRIENDS;
        }
        
        if (getFriendRequest(sender.getUniqueId(), target.getUniqueId()) != null) {
            return FriendResult.EXISTING_REQUEST;
        }
        
        FriendRequest request = new FriendRequest(sender.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());
        ManiaCore.getInstance().getDatabase().pushRecord(new net.firecraftmc.maniacore.api.records.FriendRequestRecord(request));
        if (request.getId() == 0) {
            return FriendResult.DATABASE_ERROR;
        }
        
        Redis.pushFriendRequest(request);
        return FriendResult.SUCCESS;
    }
    
    public Friendship getFriendship(UUID uuid1, UUID uuid2) {
        for (Friendship friendship : Redis.getFriendships()) {
            if (friendship.getPlayer1().equals(uuid1) && friendship.getPlayer2().equals(uuid2)) {
                return friendship;
            } else if (friendship.getPlayer1().equals(uuid2) && friendship.getPlayer2().equals(uuid1)) {
                return friendship;
            }
        }
        
        return null;
    }
    
    public List<Friendship> getFriendships(UUID player) {
        List<Friendship> friendships = new ArrayList<>();
        for (Friendship friendship : Redis.getFriendships()) {
            if (friendship.getPlayer1().equals(player) || friendship.getPlayer2().equals(player)) {
                friendships.add(friendship);
            }
        }
        
        return friendships;
    }
    
    public List<FriendRequest> getFriendRequestsByRequester(UUID requester) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        for (FriendRequest friendRequest : Redis.getFriendRequests()) {
            if (friendRequest.getSender().equals(requester)) {
                friendRequests.add(friendRequest);
            }
        }
        return friendRequests;
    }
    
    public List<FriendRequest> getFriendRequestsByTarget(UUID target) {
        List<FriendRequest> friendRequests = new ArrayList<>();
        for (FriendRequest friendRequest : Redis.getFriendRequests()) {
            if (friendRequest.getTo().equals(target)) {
                friendRequests.add(friendRequest);
            }
        }
        return friendRequests;
    }
     
    public List<net.firecraftmc.maniacore.api.friends.FriendNotification> getNotfications(UUID uuid) {
        List<net.firecraftmc.maniacore.api.friends.FriendNotification> notifications = new ArrayList<>();
        for (net.firecraftmc.maniacore.api.friends.FriendNotification friendNotification : Redis.getFriendNotifications()) {
            if (friendNotification.getSender().equals(uuid)) {
                notifications.add(friendNotification);
            }
        }
        return notifications;
    }
    
    public FriendRequest getFriendRequest(UUID uuid1, UUID uuid2) {
        for (FriendRequest request : Redis.getFriendRequests()) {
            if (request.getSender().equals(uuid1) && request.getTo().equals(uuid2)) {
                return request;
            } else if (request.getSender().equals(uuid2) && request.getTo().equals(uuid1)) {
                return request;
            }
        }
        
        return null;
    }
    
    public Pair<FriendResult, Friendship> removeFriend(User user, User target) {
        Friendship friendship = getFriendship(user.getUniqueId(), target.getUniqueId());
        if (friendship == null) {
            return new Pair<>(FriendResult.NOT_FRIENDS, null);
        }
        
        Redis.deleteFriendship(friendship);
        ManiaCore.getInstance().getDatabase().deleteRecord(new net.firecraftmc.maniacore.api.records.FriendshipRecord(friendship));
        return new Pair<>(FriendResult.SUCCESS, friendship);
    }
    
    public void addNotification(FriendNotification friendNotification) {
        ManiaCore.getInstance().getDatabase().pushRecord(new net.firecraftmc.maniacore.api.records.FriendNotificationRecord(friendNotification));
        Redis.pushFriendNotification(friendNotification);
    }
    
    private Pair<FriendResult, FriendRequest> handleRequest(FriendRequest request, User user, User target) {
        if (getFriendship(user.getUniqueId(), target.getUniqueId()) != null) {
            return new Pair<>(FriendResult.ALREADY_FRIENDS, null);
        }
        
        if (request == null) {
            return new Pair<>(FriendResult.NO_REQUEST, null);
        }
    
        if (request.getSender().equals(user.getUniqueId())) {
            return new Pair<>(FriendResult.REQUEST_SENDER, null);
        }
        return null;
    }
    
    public Pair<FriendResult, FriendRequest> acceptRequest(User user, User target) {
        FriendRequest request = getFriendRequest(user.getUniqueId(), target.getUniqueId());
        Pair<FriendResult, FriendRequest> handleRequestResult = handleRequest(request, user, target);
        if (handleRequestResult != null) {
            return handleRequestResult;
        }
    
        Friendship friendship = new Friendship(user.getUniqueId(), target.getUniqueId(), System.currentTimeMillis());
        ManiaCore.getInstance().getDatabase().pushRecord(new FriendshipRecord(friendship));
        if (friendship.getId() == 0) {
            return new Pair<>(FriendResult.DATABASE_ERROR, request);
        }
        Redis.pushFriendship(friendship);
        Redis.deleteFriendRequest(request);
        return new Pair<>(FriendResult.SUCCESS, request);
    }
    
    public Pair<FriendResult, FriendRequest> denyRequest(User user, User target) {
        FriendRequest request = getFriendRequest(user.getUniqueId(), target.getUniqueId());
        Pair<FriendResult, FriendRequest> handleRequestResult = handleRequest(request, user, target);
        if (handleRequestResult != null) {
            return handleRequestResult;
        }
        
        Redis.deleteFriendRequest(request);
        return new Pair<>(FriendResult.SUCCESS, request);
    }
}
