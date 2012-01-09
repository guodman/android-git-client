package org.guodman.android.gitclient

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.JGitInternalException
import java.io.File
import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import android.content.Context
import android.content.Intent

class GitClientActivity extends Activity {
    val TAG = "GitClientActivity";
    var init: Button = null
    var repoPath: TextView = null
    var mkdir: Button = null
    var clonePath: TextView = null
    var cloneButton: Button = null
    var username: TextView = null
    var password: TextView = null

    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        username = this.findViewById(R.id.repoUsername).asInstanceOf[TextView]
        password = this.findViewById(R.id.repoPassword).asInstanceOf[TextView]
        init = this.findViewById(R.id.init).asInstanceOf[Button]
        init.setOnClickListener(new InitializeRepo)
        repoPath = this.findViewById(R.id.repoPath).asInstanceOf[TextView]
        mkdir = this.findViewById(R.id.mkdir).asInstanceOf[Button]
        mkdir.setOnClickListener(new CreateDirectory)
        clonePath = this.findViewById(R.id.cloneURI).asInstanceOf[TextView]
        cloneButton = this.findViewById(R.id.clone).asInstanceOf[Button]
        cloneButton.setOnClickListener(new CloneRepo)
        val log = this.findViewById(R.id.log).asInstanceOf[Button]
        log.setOnClickListener(new ViewSwitcher(this, classOf[LogActivity]))
    }

    def showConfirmMessage(text: String) {
        this.runOnUiThread(new Runnable() {
            override def run() {
                new AlertDialog.Builder(GitClientActivity.this)
                    .setMessage(text)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        override def onClick(dialog: DialogInterface, which: Int) {
                            dialog.cancel
                        }
                    })
                    .show()
            }
        })
    }

    class InitializeRepo extends OnClickListener {
        override def onClick(v: View) {
            var i = Git.init();
            i.setDirectory(new File(repoPath.getText.toString));
            try {
                i.call();
            } catch {
                case e: IllegalStateException => {
                    Toast.makeText(GitClientActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        }
    }

    class CreateDirectory extends OnClickListener {
        override def onClick(v: View) {
            var dir: String = repoPath.getText.toString
            var f = new File(dir)
            if (!f.exists) {
                f.mkdirs
            }
        }
    }

    class CloneRepo extends OnClickListener {
        override def onClick(v: View) {
            new Thread(new Runnable() {
                override def run = {
                    var cc = Git.cloneRepository()
                    cc.setCloneAllBranches(true)
                    cc.setDirectory(new File(repoPath.getText.toString))
                    cc.setURI(clonePath.getText.toString)
                    cc.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username.getText.toString, password.getText.toString))
                    try {
                        cc.call
                        showConfirmMessage("Clone Completed")
                    } catch {
                        case e: JGitInternalException => {
                            Log.d(TAG, e.toString())
                            e.printStackTrace()
                            showConfirmMessage(e.getMessage())
                        }
                    }
                }
            }).start
        }
    }
}

class ViewSwitcher (context : Context, cls : Class[_]) extends OnClickListener {
	override def onClick(v: View): Unit = {
		context.startActivity(new Intent(context, cls))
	}
}
