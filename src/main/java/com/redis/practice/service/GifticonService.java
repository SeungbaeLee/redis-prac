package com.redis.practice.service;

import com.redis.practice.constant.Event;
import com.redis.practice.domain.EventCount;
import com.redis.practice.domain.Gifticon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GifticonService {
    /**
     * redis와 상호 작용하기 위해 RedisTemplate을 사용
     */
    private final RedisTemplate<String,Object> redisTemplate;
    private static final long FIRST_ELEMENT = 0;
    private static final long LAST_ELEMENT = -1;
    private static final long PUBLISH_SIZE = 10;
    private static final long LAST_INDEX = 1;
    private EventCount eventCount;

    public void setEventCount(Event event, int queue){
        this.eventCount = new EventCount(event, queue);
    }

    /**
     *
     * Redis의 정렬 집합(Sorted Set)은 멤버와 각 멤버에 연결된 점수(순위)로 구성된 데이터인데
     * 이 멤버를 삽입하거나 삭제할 때마다,
     * 자동으로 정렬되므로 순서를 신경쓰지 않고 데이터를 저장하고 검색하기에 이상적이다.
     *
     * 해당 코드에서는 opsForZSet() 메소드를 사용하여 Redis에 Sorted Set과 상호 작용하는 ZSetOperation 인터페이스의
     * 구현체를 얻을 수 있었다.
     */

    public void addQueue(Event event){
        final String people = Thread.currentThread().getName();
        final long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(event.toString(), people, (int) now);
        log.info("대기열에 추가 - {} ({}초)", people, now);
        /**
         * opsForZset().add(key, value, score)을 사용하여 정렬집합에 참가자 추가
         * 해당 코드에선 key는 이벤트의 이름, value는 참가자의 이름(여기서는 Thread), score는 현재 시간을 나타낸다.
         */
    }

    public void getOrder(Event event){
        final long start = FIRST_ELEMENT;
        final long end = LAST_ELEMENT;

        Set<Object> queue = redisTemplate.opsForZSet().range(event.toString(), start, end);

        for (Object people : queue) {
            Long rank = redisTemplate.opsForZSet().rank(event.toString(), people);
            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
            /**
             * opsForZset().range(key, start, end)를 사용하여 정렬 집합에서 특정 범위의 멤버를 가져온다.
             * opsForZSet().remove(key, value)를 사용하여 정렬 집합에서 참가자를 제거한다.
             */
        }
    }

    public void publish(Event event){
        final long start = FIRST_ELEMENT;
        final long end = PUBLISH_SIZE - LAST_INDEX;

        Set<Object> queue = redisTemplate.opsForZSet().range(event.toString(), start, end);
        for (Object people : queue) {
            final Gifticon gifticon = new Gifticon(event);
            log.info("'{}'님의 {} 기프티콘이 발급되었습니다 ({})",people, gifticon.getEvent().getName(), gifticon.getCode());
            redisTemplate.opsForZSet().remove(event.toString(), people);
            this.eventCount.decrease();
        }
    }

    public boolean validEnd(){
        return this.eventCount != null
                ? this.eventCount.end()
                : false;
    }

    public long getSize(Event event){
        return redisTemplate.opsForZSet().size(event.toString());
    }

}