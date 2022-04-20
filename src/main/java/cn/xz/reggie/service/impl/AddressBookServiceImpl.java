package cn.xz.reggie.service.impl;

import cn.xz.reggie.common.BaseThreadLocalContext;
import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.AddressBook;
import cn.xz.reggie.mapper.AddressBookMapper;
import cn.xz.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public R<AddressBook> saveAddr(AddressBook addressBook) {
        //设置地址簿中的user_id标识是哪一位用户的
        addressBook.setUserId(BaseThreadLocalContext.getCurrentId());
        save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 查询该用户全部地址
     * @param addressBook
     * @return
     */
    @Override
    public R<List<AddressBook>> listAddr(AddressBook addressBook) {
        //log.info("adderssBook={}",addressBook.toString());
        //设置当前用户Id
        addressBook.setUserId(BaseThreadLocalContext.getCurrentId());
        //条件构造器
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        wrapper.orderByDesc(AddressBook::getIsDefault);
        //查询
        List<AddressBook> bookList = list(wrapper);

        return R.success(bookList);
    }

    @Override
    public R<AddressBook> setDefaultAddr(AddressBook addressBook) {
        //根据user_id查询用户地址
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseThreadLocalContext.getCurrentId());
        List<AddressBook> addressBooks = list(wrapper);
        addressBooks = addressBooks.stream().map((item)->{
            item.setIsDefault(0);
            return item;
        }).collect(Collectors.toList());
        //将查询出来的用户地址先全部置为0
        updateBatchById(addressBooks);

        //将当前地址的默认值设置为1
        addressBook.setIsDefault(1);
        this.updateById(addressBook);
        //返回
        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public R<AddressBook> getDeaultAddr() {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseThreadLocalContext.getCurrentId())
                .eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = this.getOne(wrapper);
        return addressBook==null?R.error("没有找到该地址"):R.success(addressBook);
    }

    /**
     * 根据id查地址
     * @param id
     * @return
     */
    @Override
    public R<AddressBook> getAddrById(Long id) {
        AddressBook addressBook = this.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }

    }

    /**
     * 修改用户地址
     * @param addressBook
     * @return
     */
    @Override
    public R<String> updateAddr(AddressBook addressBook) {
        this.updateById(addressBook);
        return R.success("修改地址成功");
    }
}
