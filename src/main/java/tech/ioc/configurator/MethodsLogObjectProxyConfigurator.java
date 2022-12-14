package tech.ioc.configurator;

import lombok.extern.log4j.Log4j2;
import tech.ioc.configurator.interfaces.ProxyObjectConfigurator;
import tech.ioc.dto.BeanContainer;
import tech.ioc.infrastucture.ApplicationContext;

@Log4j2
public class MethodsLogObjectProxyConfigurator implements ProxyObjectConfigurator {

    public MethodsLogObjectProxyConfigurator(ApplicationContext context) {
    }

    @Override
    public void configure(BeanContainer container, ApplicationContext context) {
//        Object oldProxy = container.getProxy();
//        container.setProxy(Enhancer.create(container.getImplClass(), new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                log.debug(method.getName() + " " + Arrays.toString(args));
//                return method.invoke(oldProxy, args);
//            }
//                }
//        ));
    }
}
