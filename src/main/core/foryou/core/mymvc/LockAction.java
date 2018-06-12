package foryou.core.mymvc;

import java.util.UUID;

import foryou.core.base.BaseController;
import foryou.core.util.DistributedRedisLock;

/** 
* @author 罗林 E-mail:1071893649@qq.com 
* @version 创建时间：2018年6月11日 下午4:38:30 
* 类说明 
*/
public class LockAction extends BaseController {
	
	public void testLock(){
		DistributedRedisLock.tryLock("key",UUID.randomUUID().toString(), 60 * 1000);
		System.out.println("当前线程：" + Thread.currentThread().getName());
	}

}
