package com.csfrez.tool.download;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.event.DownloadLifecycleListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomLifecycleListener implements DownloadLifecycleListener {

    /**
     * 下载开始。
     */
    @Override
    public void onStart(DownloadContext context) {
        log.info("onStart");

    }

    /**
     * 下载完成。
     */
    @Override
    public void onComplete(DownloadContext context) {
        log.info("onComplete");
    }
}