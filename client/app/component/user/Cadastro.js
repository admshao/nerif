Ext.define('Nerif.component.user.Cadastro', {
    extend: 'Ext.window.Window',

    modal: true,
    layout: 'fit',
    title: 'Cadastro de usuários',

    initComponent: function () {
        var obj = this;

        var usuarioidHdn = Ext.create('Ext.form.Hidden', {
            name: 'id'
        });

        var nomeUsuarioText = Ext.create('Ext.form.Text', {
            name: 'nome',
            allowBlank: false,
            fieldLabel: 'Nome'
        });

        var emailUsuarioText = Ext.create('Ext.form.Text', {
            name: 'email',
            allowBlank: false,
            fieldLabel: 'Email',
            vtype: 'email'
        });

        var telefoneUsuarioText = Ext.create('Ext.form.Text', {
            name: 'telefone',
            fieldLabel: 'Telefone',
            regex: /^[+][0-9]*$/,
            emptyText: '+999999999999'
        });

        var cancelarBtn = Ext.create('Ext.button.Button', {
            text: 'Cancelar',
            handler: function () {
                obj.close();
            }
        });

        var confirmarBtn = Ext.create('Ext.button.Button', {
            formBind: true,
            text: 'Salvar',
            handler: function () {
                obj.fireEvent('usuariosalvo', formpanel.getValues());
                obj.close();
            }
        });

        var formpanel = Ext.create('Ext.form.Panel', {
            bodyPadding: '10px',
            fieldDefaults: {
                labelAlign: 'right',
                width: 600
            },
            items: [usuarioidHdn, nomeUsuarioText, emailUsuarioText, telefoneUsuarioText],
            buttons: ['->', cancelarBtn, confirmarBtn]
        });

        this.items = [formpanel];

        this.on('afterrender', function () {
            formpanel.isValid();
        });

        this.editar = function (rec) {
            obj.show();

            formpanel.loadRecord(rec);
            formpanel.isValid();
        };

        this.callParent();
    }
});