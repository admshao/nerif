Ext.define('Nerif.view.tab.Estatistica', {
    extend: 'Ext.form.Panel',

    title: 'Estatísticas',
    layout: 'border',
    bodyPadding: '10px',
    
    initComponent: function() {
    	var obj = this;
    	    	
    	var states = Ext.create('Ext.data.Store', {
    	    fields: ['valor', 'descricao'],
    	    data : [
    	        {"valor":"url", "descricao":"Total de requisições por hora"}
    	    ]
    	});

    	var opcoesCombo = Ext.create('Ext.form.ComboBox', {
    	    fieldLabel: 'Opções',
    	    labelAlign: 'right',
    	    width: 450,
    	    store: states,
    	    queryMode: 'local',
    	    editable: false,
    	    displayField: 'descricao',
    	    valueField: 'valor',
    	    value: 'url'
    	});
    	
    	var filtroText = Ext.create('Ext.form.Text', {
    		fieldLabel: 'Filtro',
    		labelAlign: 'right',
    		width: 300,
    		value: '/Login'
    	});
    	
    	var opcoesContainer = Ext.create('Ext.form.FieldContainer', {
    		items: [opcoesCombo, filtroText],
    		layout: 'hbox',
    		region: 'north',
    		height: 40
    	});
    	
    	this.items = [opcoesContainer, {
		   xtype: 'cartesian',
		   region: 'center',
		   insetPadding: 40,
		   store: {
			   fields: ['horario', 'quantidade'],
		       data: [
	              { 'horario': '00:00', 'quantidade': 59 },
	              { 'horario': '01:00', 'quantidade': 75 },
	              { 'horario': '02:00', 'quantidade': 68 },
	              { 'horario': '03:00', 'quantidade': 51 },
	              { 'horario': '04:00', 'quantidade': 47 },
	              { 'horario': '05:00', 'quantidade': 42 },
	              { 'horario': '06:00', 'quantidade': 25 },
	              { 'horario': '07:00', 'quantidade': 52 },
	              { 'horario': '08:00', 'quantidade': 169 },
	              { 'horario': '09:00', 'quantidade': 199 },
	              { 'horario': '10:00', 'quantidade': 191 },
	              { 'horario': '11:00', 'quantidade': 202 },
	              { 'horario': '12:00', 'quantidade': 174 },
	              { 'horario': '13:00', 'quantidade': 208 },	              
	              { 'horario': '14:00', 'quantidade': 229 },	              
	              { 'horario': '15:00', 'quantidade': 213 },	              
	              { 'horario': '16:00', 'quantidade': 217 },	              
	              { 'horario': '17:00', 'quantidade': 162 },	              
	              { 'horario': '18:00', 'quantidade': 162 },
	              { 'horario': '19:00', 'quantidade': 152 },	              
	              { 'horario': '20:00', 'quantidade': 181 },
	              { 'horario': '21:00', 'quantidade': 137 },
	              { 'horario': '22:00', 'quantidade': 90 },
	              { 'horario': '23:00', 'quantidade': 190 }
              ]
		   },
		   axes: [{
		       type: 'numeric',
		       position: 'left',
		       fields: ['quantidade'],
		       grid: true,
		       minimum: 0
		   }, {
		       type: 'category',
		       position: 'bottom',
		       fields: ['horario']
		   }],
		   series: [{
			   type: 'bar',
		       xField: 'horario',
		       yField: 'quantidade',
		       subStyle: {
		           fill: ['#5fa2dd'],
		           stroke: '#5fa2dd'
		       }
		   }]
    	}];
    	
    	this.callParent();
    }
});