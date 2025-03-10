package vn.com.unit.miragesql.miragesql.integration.guice;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.session.Session;
import vn.com.unit.miragesql.miragesql.session.SessionFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

/**
 * The Module implementation to use Mirage-SQL with Google Guice.
 *
 * @author Naoki Takezoe
 */
public class MirageModule extends AbstractModule {

//	@Override
    protected void configure() {
        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(Transactional.class),
                new TransactionInterceptor());
    }

    @Provides
    @Singleton
    public Session getSession(){
        return SessionFactory.getSession();
    }

    @Provides
    @Singleton
    public SqlManager getSqlManager(Session session){
        return session.getSqlManager();
    }

}
