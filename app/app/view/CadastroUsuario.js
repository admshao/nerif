Ext.define('Nerif.view.CadastroUsuario', {
	extend: 'Ext.window.Window',

	modal: true,
	layout: 'fit',
	title: 'Cadastro de usuário',

	initComponent: function () {
		var obj = this;

		var usuarioid = Ext.create('Ext.form.Hidden', {
			name: 'id'
		});

		var nomeUsuarioText = Ext.create('Ext.form.Text', {
			name: 'nome',
			fieldLabel: 'Nome',
			allowBlank: false
		});

		var emailUsuarioText = Ext.create('Ext.form.Text', {
			name: 'email',
			fieldLabel: 'Email',
			allowBlank: false
		});

		var formpanel = Ext.create('Ext.tab.Panel', {

		});

		this.items = [formpanel];

		this.callParent();
	}
});