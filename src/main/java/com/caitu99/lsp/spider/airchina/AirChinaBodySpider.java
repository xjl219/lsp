/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.lsp.spider.airchina;

import com.caitu99.lsp.model.spider.Envelope;
import com.caitu99.lsp.model.spider.MailSpiderEvent;
import com.caitu99.lsp.spider.MailBodySpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 国航积分抓取事件
 *
 * @author chencheng
 * @Description: (类职责详细描述, 可空)
 * @ClassName: AirChinaBodySpider
 * @date 2015年11月12日 下午12:11:03
 * @Copyright (c) 2015-2020 by caitu99
 */
public class AirChinaBodySpider extends MailBodySpider {

    private final static Logger logger = LoggerFactory.getLogger(AirChinaBodySpider.class);

    private static final int INTERVAL_TIME = 2000;

    public void run() {
        while (!isInterrupted()) {

            long t = new Date().getTime();
            MailSpiderEvent event = null;
            logger.debug("air china body spider do polling...");
            try {
                event = events.takeFirst();
            } catch (InterruptedException e1) {
                logger.error("air china body spider thread is interrupted");
            }
            if (event == null) continue;
            ConcurrentLinkedDeque<Envelope> envelopes = event.getEnvelopes();
            long lastTime = event.getLastGetTime();
            long interval = t - lastTime;
            // 如果不是获取邮件状态，就丢弃
            if (!envelopes.isEmpty()) {
                if (interval >= INTERVAL_TIME) {
                    // 如果上次发送时间和这次差2s以上，则需要发送
                    mailSpider.onEvent(event);
                    event.setLastGetTime(t);
                    events.addLast(event);
                } else {
                    events.addFirst(event);
                    try {
                        Thread.sleep(INTERVAL_TIME - interval);
                    } catch (InterruptedException e) {
                        logger.error("air china body spider thread is interrupted");
                    }
                }
            }
        }
    }
}
