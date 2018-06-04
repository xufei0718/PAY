package com.mybank.pc.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mybank.pc.merchant.user.MerchantUserCtr;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.cybermkd.mongo.plugin.MongoJFinalPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.JsonRender;
import com.jfinal.template.Engine;
import com.jfplugin.mail.MailPlugin;
import com.mybank.pc.CMNCtr;
import com.mybank.pc.Consts;
import com.mybank.pc.IndexCtr;
import com.mybank.pc.admin.HomeCtr;
import com.mybank.pc.admin.LoginCtr;
import com.mybank.pc.admin.art.ArtCtr;
import com.mybank.pc.admin.param.ParamCtr;
import com.mybank.pc.admin.res.ResCtr;
import com.mybank.pc.admin.role.RoleCtr;
import com.mybank.pc.admin.taxonomy.TaxCtr;
import com.mybank.pc.admin.user.UserCtr;
import com.mybank.pc.collection.api.CEntrustInterface;
import com.mybank.pc.collection.api.CTradeInterface;
import com.mybank.pc.collection.clear.CClearCtr;
import com.mybank.pc.collection.entrust.CEntrustCtr;
import com.mybank.pc.collection.trade.BackRcvResponse;
import com.mybank.pc.collection.trade.CBatchTradeCtr;
import com.mybank.pc.collection.trade.CTradeCtr;
import com.mybank.pc.interceptors.AdminAAuthInterceptor;
import com.mybank.pc.interceptors.AdminIAuthInterceptor;
import com.mybank.pc.interceptors.ExceptionInterceptor;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.ResKit;
import com.mybank.pc.merchant.cust.MerchantCustCtr;
import com.mybank.pc.merchant.info.MerchantInfoCtr;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Created by yuhaihui8913 on 2017/11/14.
 */
public class CoreConfig extends JFinalConfig {

	static ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
			.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

	static {
		root.setLevel(ch.qos.logback.classic.Level.INFO);
	}

	@Override
	public void configConstant(Constants constants) {
		constants.setDevMode(ResKit.getConfigBoolean("devMode"));
		// constants.setMainRenderFactory(new BeetlRenderFactory());
		constants.setError500View("/WEB-INF/template/common/500.html");
		constants.setError404View("/WEB-INF/template/common/404.html");
		constants.setError403View("/WEB-INF/template/common/403.html");
		constants.setError401View("/WEB-INF/template/common/401.html");
		constants.setEncoding("UTF-8");
		constants.setJsonFactory(new FastJsonFactory());
		constants.setLogFactory(new Log4jLogFactory());
		constants.setJsonDatePattern(DateKit.STR_DATEFORMATE);

	}

	@Override
	public void configRoute(Routes routes) {
		routes.add(new Routes() {
			@Override
			public void config() {
				addInterceptor(new AdminAAuthInterceptor());
				add("/ad00", ParamCtr.class);
				add("/ad01", UserCtr.class);
				add("/ad02", RoleCtr.class);
				add("/ad03", ResCtr.class);
				add("/ad04", ArtCtr.class);
				add("/ad05", TaxCtr.class);
				add("/ad06", LoginCtr.class);
			}
		});

		routes.add(new Routes() {
			@Override
			public void config() {
				add("/coll/trade", CTradeCtr.class);
				add("/coll/batch", CBatchTradeCtr.class);
				add("/coll/entrust", CEntrustCtr.class);
				add("/coll/backRcvResponse", BackRcvResponse.class);
			}
		});

		routes.add(new Routes() {
			@Override
			public void config() {
				add("/home", HomeCtr.class);
			}
		});
		routes.add(new Routes() {
			@Override
			public void config() {
				add("/cmn", CMNCtr.class);
			}
		});

		routes.add(new Routes() {
			@Override
			public void config() {
				add("/mer00", MerchantInfoCtr.class);
			}
		});
		routes.add(new Routes() {
			@Override
			public void config() {
				add("/mer01", MerchantCustCtr.class);
			}
		});
		routes.add(new Routes() {
			@Override
			public void config() {
				add("/mer02", MerchantUserCtr.class);
			}
		});

		// 清分模块路由
		routes.add(new Routes() {
			@Override
			public void config() {
				add("/cc", CClearCtr.class);
			}
		});
		routes.add(new Routes() {
			@Override
			public void config() {
				add("/", IndexCtr.class);
			}
		});

		routes.add(new Routes() {
			@Override
			public void config() {
				add("/coll/api/entrust", CEntrustInterface.class);
				add("/coll/api/trade", CTradeInterface.class);
			}
		});
	}

	@Override
	public void configEngine(Engine engine) {
		engine.addSharedObject("ctx", JFinal.me().getContextPath());
		engine.addSharedMethod(new StrUtil());
		engine.addSharedObject("cKit", new CollectionUtil());
		engine.setDevMode(ResKit.getConfigBoolean("devMode", true));
		// 使用JF模板渲染通用页面
		// engine.addSharedFunction("/WEB-INF/template/www/css.html");
		// engine.addSharedFunction("/WEB-INF/template/www/js.html");
		// engine.addSharedFunction("/WEB-INF/template/admin/_layout.html");
		engine.addSharedFunction("/WEB-INF/template/www/_layout.html");
	}

	@Override
	public void configPlugin(Plugins plugins) {
		// 开启druid数据库连接池
		DruidPlugin druidPlugin = createDruidPlugin();
		// StatFilter提供JDBC层的统计信息
		druidPlugin.addFilter(new StatFilter());
		// WallFilter的功能是防御SQL注入攻击
		WallFilter wallDefault = new WallFilter();
		wallDefault.setDbType("mysql");
		druidPlugin.addFilter(wallDefault);
		druidPlugin.setInitialSize(ResKit.getConfigInt("db.default.poolInitialSize"));
		druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(ResKit.getConfigInt("db.default.poolMaxSize"));
		druidPlugin.setTimeBetweenConnectErrorMillis(ResKit.getConfigInt("db.default.connectionTimeoutMillis"));
		plugins.add(druidPlugin);
		// 开启DB+record 映射关系插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		_MappingKit.mapping(arp);
		arp.getEngine().setDevMode(true);
		arp.getEngine().addSharedMethod(new StrUtil());
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath() + "/sql");
		arp.addSqlTemplate("all.sql");
		arp.setShowSql(ResKit.getConfigBoolean("devMode")?true:false);
		plugins.add(arp);
		// 开启eheache缓存
		plugins.add(new EhCachePlugin());
		// 计划任务插件,开发模式默认关闭，如果需要测试自行打开，防止其他自动发起的对外界系统调用进行回掉
//		if(!ResKit.getConfigBoolean("devMode")) {
			Cron4jPlugin cron4jPlugin = new Cron4jPlugin("task.properties", "cron4j");
			plugins.add(cron4jPlugin);
//		}
		MongoJFinalPlugin jFinalPlugin = new MongoJFinalPlugin();
		jFinalPlugin.add(ResKit.getConfig("mongodb.ip"), ResKit.getConfigInt("mongodb.port"));
		jFinalPlugin.setDatabase(ResKit.getConfig("mongodb.db"));
		jFinalPlugin.auth(ResKit.getConfig("mongodb.user"), ResKit.getConfig("mongodb.pwd"));
		plugins.add(jFinalPlugin);
		// mail 插件
		plugins.add(new MailPlugin(PropKit.use("mail.properties").getProperties()));

	}

	private DruidPlugin createDruidPlugin() {
		DruidPlugin druidDefault = new DruidPlugin(ResKit.getConfig("db.default.url"),
				ResKit.getConfig("db.default.user"), ResKit.getConfig("db.default.password"),
				ResKit.getConfig("db.default.driver"));
		return druidDefault;
	}

	@Override
	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new ExceptionInterceptor());
		interceptors.add(new AdminIAuthInterceptor());
	}

	@Override
	public void configHandler(Handlers handlers) {
		if (JFinal.me().getConstants().getDevMode()) {
			handlers.add(new Handler() {
				@Override
				public void handle(String s, HttpServletRequest httpServletRequest,
						HttpServletResponse httpServletResponse, boolean[] booleans) {
					if (s.equals("/static/swagger/index.html") || s.equals("/Api")) {
						return;
					} else {
						next.handle(s, httpServletRequest, httpServletResponse, booleans);
					}
				}
			});
		}
	}

	@Override
	public void afterJFinalStart() {
		super.afterJFinalStart();
		CoreData.loadAllCache();
		// 设置请求以json格式返回的时候，排除掉请求中用户身份信息相关的数据。
		JsonRender.addExcludedAttrs(Consts.CURR_USER, Consts.CURR_USER_MER, Consts.CURR_USER_RESES,
				Consts.CURR_USER_ROLES);
		try {
			// 初始化银联代收SDK
			Class.forName("com.mybank.pc.kits.unionpay.acp.SDK");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
