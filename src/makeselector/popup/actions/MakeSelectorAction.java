package makeselector.popup.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class MakeSelectorAction implements IObjectActionDelegate {

	/**
	 * Constructor for Action1.
	 */
	public MakeSelectorAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		if(mFiles.size() < 1) {
			return;
		}
		
		IPath path = mFiles.get(0).getParent().getLocation();
		
		String normal = null, pressed = null, selected = null, checked = null;
		for(IFile item : mFiles) {
			String name = item.getName();
			name = name.substring(0, name.indexOf("."));
			if(name.endsWith("_n")) {
				normal = name;
			} else if(name.endsWith("_s")) {
				selected = name;
			} else if(name.endsWith("_p")) {
				pressed = name;
			} else if(name.endsWith("_c")) {
				checked = name;
			}
		}
		
		if(normal == null) {
			System.out.println("Make selector : _n file not found! terminate.");
			mFiles.clear();
			return;
		}
		
		String newline = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append(newline);
		sb.append("<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">").append(newline);
		if(pressed != null)
			sb.append("  <item android:state_pressed=\"true\" android:drawable=\"@drawable/").append(pressed).append("\" />").append(newline);
		if(selected != null)
			sb.append("  <item android:state_selected=\"true\" android:drawable=\"@drawable/").append(selected).append("\" />").append(newline);
		if(checked != null)
			sb.append("  <item android:state_checked=\"true\" android:drawable=\"@drawable/").append(checked).append("\" />").append(newline);
		if(normal != null)
			sb.append("  <item android:drawable=\"@drawable/").append(normal).append("\" />").append(newline);
		sb.append("</selector>");
		
		int endIndex = normal.lastIndexOf("_n");
		String selectorname = normal.substring(0, endIndex) + "_selector.xml"; 
		
		System.out.println("Make selector : " + selectorname	);
		System.out.println(sb.toString());
		
		File target = new File(path.toFile(), selectorname);
		
		try {
			FileWriter fw = new FileWriter(target, false);
			fw.write(sb.toString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			mFiles.get(0).getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		mFiles.clear();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		Iterator<?> it = sel.iterator();
		while(it.hasNext()) {
			addSelectedFile((IFile) it.next());
		}
	}
	
	private Vector<IFile> mFiles = new Vector<IFile>();

	private void addSelectedFile(IFile file) {
		mFiles.add(file);
	}
}
