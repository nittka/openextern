package hr.ja.openextern.popup.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import hr.ja.openextern.Activator;
import hr.ja.openextern.preferences.PreferenceConstants;

public class OpenShellAction extends BaseOpenAction{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String commandOpenFolder = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_OPEN_SHELL);
		execCommand(event, commandOpenFolder, "shell");
		return null;
	}
}
