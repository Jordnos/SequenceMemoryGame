package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// this class creates a hashmap of accounts with username key pointing to the password
// has methods to access an account
public class AccountList implements Writable {
    private Map<String, UserAccount> list;

    // EFFECTS:  initializes an empty list of accounts
    public AccountList() {
        list = new HashMap<>();
    }

    // MODIFIES: this
    // EFFECTS:  creates an account and adds it to the accountlist if account with username doesn't already exist
    //           otherwise return false
    public boolean addAccount(String username, String password) {
        UserAccount acc = new UserAccount(username, password);
        if (usernameTaken(username)) {
            return false;
        }
        list.put(username,acc);
        EventLog.getInstance().logEvent(new Event("New User Created - User: "
                + username + " Password: " + password));
        return true;
    }

    // EFFECTS:  return true if username and password match an account on the accountlist
    public boolean loginAccount(String username, String password) {
        if (list.containsKey(username) && list.get(username).getPassword().equals(password)) {
            EventLog.getInstance().logEvent(new Event("User logged in - User: " + username));
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS:  return true if username and password match an account, and it is successfully
    //           removed from the accountlist, return false otherwise
    public boolean deleteAccount(String username, String password) {
        if (list.containsKey(username) && list.get(username).getPassword().equals(password)) {
            list.remove(username);
            EventLog.getInstance().logEvent(new Event("Account Deleted - User: " + username));
            return true;
        }
        return false;
    }

    // EFFECTS:  return true if username is taken, false otherwise
    public boolean usernameTaken(String username) {
        return list.containsKey(username);
    }

    // MODIFIES: this
    // EFFECTS:  changes password to new password, if successful return true,
    //           otherwise return false
    public boolean changePass(String username, String password) {
        if (list.containsKey(username)) {
            list.get(username).setPassword(password);
            EventLog.getInstance().logEvent(new Event("Password Changed - User: "
                    + username + " New password: " + password));
            return true;
        }
        return false;
    }

    public Map<String, UserAccount> getList() {
        return Collections.unmodifiableMap(list);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("accountList", accountListToJson());
        return json;
    }

    // EFFECTS: returns things in this account list as a JSON array
    private JSONArray accountListToJson() {
        JSONArray jsonArray = new JSONArray();

        for (HashMap.Entry<String,UserAccount> acc : list.entrySet()) {
            jsonArray.put(acc.getValue().toJson());
        }

        return jsonArray;
    }
}