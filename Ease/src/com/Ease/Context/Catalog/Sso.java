package com.Ease.Context.Catalog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.simple.JSONObject;

import com.Ease.Utils.DataBaseConnection;
import com.Ease.Utils.DatabaseRequest;
import com.Ease.Utils.DatabaseResult;
import com.Ease.Utils.GeneralException;
import com.Ease.Utils.IdGenerator;
import com.Ease.Utils.ServletManager;

public class Sso {

    public enum Data {
        NOTHING,
        ID,
        NAME,
        IMG
    }

    public static List<Sso> loadSsos(DataBaseConnection db, ServletContext context) throws GeneralException {
        List<Sso> ssos = new LinkedList<Sso>();
        DatabaseResult rs = db.prepareRequest("SELECT * FROM sso").get();
        Sso sso;
        while (rs.next()) {
            int single_id = ((IdGenerator) context.getAttribute("idGenerator")).getNextId();
            sso = new Sso(rs.getString(Data.ID.ordinal()), rs.getString(Data.NAME.ordinal()), rs.getString(Data.IMG.ordinal()), single_id);
            ssos.add(sso);
        }
        return ssos;
    }

    protected List<Website> websites;
    protected Map<String, Website> websitesDBmap;
    protected Map<Integer, Website> websitesIDmap;
    protected String name;
    protected int single_id;
    protected String db_id;
    protected String img_path;

    public Sso(String db_id, String name, String img_path, int single_id) {
        this.name = name;
        this.websites = new LinkedList<Website>();
        this.websitesDBmap = new HashMap<String, Website>();
        this.websitesIDmap = new HashMap<Integer, Website>();
        this.single_id = single_id;
        this.db_id = db_id;
        this.img_path = img_path;
    }

    public void addWebsite(Website site) {
        this.websites.add(site);
        this.websitesDBmap.put(site.getDb_id(), site);
        this.websitesIDmap.put(site.getSingleId(), site);
    }

    public int getSingleId() {
        return single_id;
    }

    public void setSingleId(int singleId) {
        this.single_id = singleId;
    }

    public String getDbid() {
        return this.db_id;
    }

    public String getName() {
        return this.name;
    }

    public String getImgPath() {
        return "/resources/sso/" + img_path;
    }

    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("imgSrc", this.getImgPath());
        json.put("singleId", this.single_id);
        return json;
    }

    public void refresh(ServletManager sm) throws GeneralException {
        DataBaseConnection db = sm.getDB();
        DatabaseRequest request = db.prepareRequest("SELECT * FROM sso where id = ?");
        request.setInt(this.db_id);
        DatabaseResult rs = request.get();
        if (!rs.next())
            throw new GeneralException(ServletManager.Code.InternError, "This sso does not exist");
        this.name = rs.getString(Data.NAME.ordinal());
        this.img_path = rs.getString(Data.IMG.ordinal());
    }
}
