package org.guodman.android.gitclient

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.LinearLayout
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import java.io.File
import android.widget.TextView
import org.eclipse.jgit.lib.ObjectId

class LogActivity extends Activity {
	override def onCreate(savedInstanceState: Bundle) {
	    super.onCreate(savedInstanceState)
	    val sv = new ScrollView(this)
	    setContentView(sv)
	    val l = new LinearLayout(this)
	    sv.addView(l)
	    val repo = new Git((new RepositoryBuilder)
	            .setGitDir(new File("/mnt/sdcard/test/.git/"))
	            .readEnvironment
	            .findGitDir
	            .build)
	    val log = repo.log
	    //log.add(ObjectId.fromString("ce5124a9ea5a60aa82332b167ec09e6bfd1544fd"))
	    log.all
	    val entries = log.call
	    val iter = entries.iterator();
	    var i = 0;
	    while (iter.hasNext()) {
	        val n = iter.next()
	        i += 1;
	        val time = n.getCommitTime()
	        val timeString: String = String.valueOf(time)
	        val message = n.getFullMessage()
	        val display = new TextView(this)
	        var text: StringBuilder = new StringBuilder()
	        text.append(n.toString)
	        text.append(message)
	        display.setText(text.toString())
	        l.addView(display)
	    }
	}
}
