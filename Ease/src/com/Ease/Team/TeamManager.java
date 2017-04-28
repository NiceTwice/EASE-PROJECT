package com.Ease.Team;

import com.Ease.Utils.GeneralException;
import com.Ease.Utils.ServletManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by thomas on 28/04/2017.
 */
public class TeamManager {

    protected List<Team> teams;
    protected HashMap<Integer, Team> teamIdMap;

    public TeamManager() {
        this.teams = Team.loadTeams();
        this.teamIdMap = new HashMap<>();
        for (Team team : this.teams)
            this.teamIdMap.put(team.getDb_id(), team);

    }

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeamWithId(Integer team_id) throws GeneralException {
        Team team = this.teamIdMap.get(team_id);
        if (team == null)
            throw new GeneralException(ServletManager.Code.ClientError, "No such team");
        return team;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
        this.teamIdMap.put(team.getDb_id(), team);
    }

    public void removeTeam(Team team) {
        this.teams.remove(team);
        this.teamIdMap.remove(team.getDb_id());
    }

    public void removeTeamWithId(Integer team_id) throws GeneralException {
        Team team = this.getTeamWithId(team_id);
        this.removeTeam(team);
    }
}