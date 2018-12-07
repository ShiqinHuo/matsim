package org.matsim.contrib.hybridsim.simulation;

import org.matsim.contrib.hybridsim.utils.IdIntMapper;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.components.QSimComponentAnnotationsRegistry;
import org.matsim.core.mobsim.qsim.qnetsimengine.HybridNetworkFactory;

import com.google.inject.Provides;
import com.google.inject.Singleton;

public class HybridQSimModule extends AbstractQSimModule {
	static public String HYBRID_EXTERNAL_ENGINE_NAME = "HybridExternalEngine";

	@Override
	protected void configureQSim() {
		this.addQSimComponentBinding( HYBRID_EXTERNAL_ENGINE_NAME ).to( ExternalEngine.class ) ;
	}

	@Provides
	@Singleton
	ExternalEngine provideExternalEngine(EventsManager eventsManager, QSim qSim, IdIntMapper mapper,
			HybridNetworkFactory networkFactory) {
		ExternalEngine engine = new ExternalEngine(eventsManager, qSim, mapper);
		networkFactory.setExternalEngine(engine);
		return engine;
	}
	
	static public void configureComponents( QSimComponentAnnotationsRegistry components ) {
		components.addNamedAnnotation(HYBRID_EXTERNAL_ENGINE_NAME );
	}
}
