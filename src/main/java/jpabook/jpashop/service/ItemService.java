package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.jpa.ItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

//    private final ItemRepository itemRepository;
    private final ItemJpaRepository itemJpaRepository;

    @Transactional
    public void saveItem(Item item){
        itemJpaRepository.save(item);
    }

    @Transactional // 변경 감지 - 준영속성 해결
    public Item updateItem(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemJpaRepository.findById(itemId)
                        .orElseThrow(IllegalArgumentException::new);

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        return findItem;
    }

    public List<Item> findItems(){
        return itemJpaRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemJpaRepository.findById(itemId)
                .orElseThrow(IllegalArgumentException::new);
    }
}
