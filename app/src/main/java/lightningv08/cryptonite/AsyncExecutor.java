package lightningv08.cryptonite;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void execute(AsyncExecutable asyncExecutable) {
        executor.execute(() -> {
            asyncExecutable.doInBackground();
            handler.post(asyncExecutable::doInUIThread);
        });
    }
    public interface AsyncExecutable {
        void doInBackground();

        void doInUIThread();
    }
}
