package name.killer.Tasks;

import cn.nukkit.scheduler.PluginTask;
import name.killer.Killer;

public class VictoryTask extends PluginTask<Killer> {

    private int victory;

    public VictoryTask(Killer owner, int victory) {
        super(owner);
        this.victory = victory;
    }

    @Override
    public void onRun(int i) {

    }

}
