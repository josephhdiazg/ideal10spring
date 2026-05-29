import {
  BadgeCheck,
  Building2,
  CalendarDays,
  CircleDollarSign,
  FileCheck2,
  Home,
  Landmark,
  Percent,
  ReceiptText,
  ShieldCheck,
  Tags,
  UsersRound,
  WalletCards,
} from '@lucide/vue'

const boolOptions = [
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false },
]

const propertyUseOptions = ['RESIDENTIAL', 'COMMERCIAL', 'INDUSTRIAL', 'RURAL', 'INSTITUTIONAL']
const propertyStatusOptions = ['ACTIVE', 'INACTIVE', 'BLOCKED']
const classificationOptions = ['URBAN', 'RURAL']
const paymentMethodOptions = ['CASH', 'TRANSFER', 'DEPOSIT', 'CARD', 'OTHER']

const money = (value) => value == null ? '-' : new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
}).format(value)

const date = (value) => value ? new Intl.DateTimeFormat('es-CO').format(new Date(value)) : '-'

const nested = (path) => (row) => path.split('.').reduce((value, key) => value?.[key], row) ?? '-'

export const resources = [
  {
    key: 'municipalities',
    path: 'municipalities',
    title: 'Municipios',
    singular: 'municipio',
    eyebrow: 'Catastro',
    endpoint: '/api/v1/municipalities',
    icon: Landmark,
    accent: 'sky',
    operations: { create: true, update: true, delete: true },
    columns: [
      { key: 'name', label: 'Nombre' },
      { key: 'department', label: 'Departamento' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'name', label: 'Nombre', required: true },
      { key: 'department', label: 'Departamento', required: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'owners',
    path: 'owners',
    title: 'Propietarios',
    singular: 'propietario',
    eyebrow: 'Catastro',
    endpoint: '/api/v1/owners',
    icon: UsersRound,
    accent: 'cyan',
    operations: { create: true, update: true, delete: true },
    columns: [
      { key: 'identificationNumber', label: 'Documento' },
      { key: 'fullName', label: 'Nombre' },
      { key: 'phone', label: 'Telefono' },
      { key: 'email', label: 'Email' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'identificationNumber', label: 'Documento', required: true },
      { key: 'fullName', label: 'Nombre completo', required: true },
      { key: 'phone', label: 'Telefono', nullable: true },
      { key: 'email', label: 'Email', type: 'email', nullable: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'properties',
    path: 'properties',
    title: 'Predios',
    singular: 'predio',
    eyebrow: 'Catastro',
    endpoint: '/api/v1/properties',
    icon: Home,
    accent: 'blue',
    operations: { create: true, update: true, delete: true },
    relatedActions: [
      { label: 'Ver propietarios', method: 'GET', path: '/api/v1/properties/{propertyId}/owners' },
      { label: 'Asignar propietario', method: 'POST', path: '/api/v1/properties/{propertyId}/owners' },
      { label: 'Actualizar asignacion', method: 'PUT', path: '/api/v1/properties/{propertyId}/owners/{ownerId}' },
      { label: 'Eliminar asignacion', method: 'DELETE', path: '/api/v1/properties/{propertyId}/owners/{ownerId}' },
    ],
    columns: [
      { key: 'cadastralCode', label: 'Codigo' },
      { key: 'address', label: 'Direccion' },
      { key: 'municipality.name', label: 'Municipio', value: nested('municipality.name') },
      { key: 'propertyUse', label: 'Uso' },
      { key: 'status', label: 'Estado', type: 'status' },
      { key: 'cadastralValue', label: 'Valor', value: (row) => money(row.cadastralValue), align: 'right' },
    ],
    fields: [
      { key: 'cadastralCode', label: 'Codigo catastral', required: true },
      { key: 'address', label: 'Direccion', required: true },
      { key: 'propertyUse', label: 'Uso', type: 'select', options: propertyUseOptions, required: true },
      { key: 'status', label: 'Estado', type: 'select', options: propertyStatusOptions, default: 'ACTIVE' },
      { key: 'cadastralValue', label: 'Valor catastral', type: 'number', required: true },
      { key: 'municipalityId', label: 'ID municipio', type: 'number', required: true, fromRow: (row) => row.municipality?.id ?? '' },
    ],
  },
  {
    key: 'fiscal-years',
    path: 'fiscal-years',
    title: 'Vigencias fiscales',
    singular: 'vigencia fiscal',
    eyebrow: 'Configuracion tributaria',
    endpoint: '/api/v1/fiscal-years',
    icon: CalendarDays,
    accent: 'indigo',
    operations: { create: true, update: true, delete: true },
    extraReads: [{ label: 'Vigencia activa', method: 'GET', path: '/api/v1/fiscal-years/active' }],
    columns: [
      { key: 'year', label: 'Anio' },
      { key: 'description', label: 'Descripcion' },
      { key: 'startDate', label: 'Inicio', value: (row) => date(row.startDate) },
      { key: 'endDate', label: 'Fin', value: (row) => date(row.endDate) },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'year', label: 'Anio', type: 'number', required: true },
      { key: 'description', label: 'Descripcion', nullable: true },
      { key: 'startDate', label: 'Fecha inicio', type: 'date', required: true },
      { key: 'endDate', label: 'Fecha fin', type: 'date', required: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'tax-rates',
    path: 'tax-rates',
    title: 'Tarifas prediales',
    singular: 'tarifa',
    eyebrow: 'Configuracion tributaria',
    endpoint: '/api/v1/tax-rates',
    icon: Percent,
    accent: 'violet',
    operations: { create: true, update: true, delete: true },
    extraReads: [{ label: 'Por vigencia', method: 'GET', path: '/api/v1/tax-rates/fiscal-year/{fiscalYearId}' }],
    columns: [
      { key: 'year', label: 'Anio' },
      { key: 'classification', label: 'Clasificacion' },
      { key: 'ratePerThousand', label: 'Tarifa x mil' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'fiscalYearId', label: 'ID vigencia', type: 'number', required: true },
      { key: 'classification', label: 'Clasificacion', type: 'select', options: classificationOptions, required: true },
      { key: 'ratePerThousand', label: 'Tarifa por mil', type: 'number', required: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'tax-benefits',
    path: 'tax-benefits',
    title: 'Beneficios tributarios',
    singular: 'beneficio',
    eyebrow: 'Configuracion tributaria',
    endpoint: '/api/v1/tax-benefits',
    icon: Tags,
    accent: 'emerald',
    operations: { create: true, update: true, delete: true },
    extraReads: [{ label: 'Por clasificacion', method: 'GET', path: '/api/v1/tax-benefits/by-classification?classification=URBAN' }],
    columns: [
      { key: 'code', label: 'Codigo' },
      { key: 'name', label: 'Nombre' },
      { key: 'discountPercentage', label: 'Descuento' },
      { key: 'applicableClassification', label: 'Clasificacion' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'code', label: 'Codigo', required: true },
      { key: 'name', label: 'Nombre', required: true },
      { key: 'description', label: 'Descripcion', nullable: true },
      { key: 'discountPercentage', label: 'Porcentaje descuento', type: 'number', required: true },
      { key: 'applicableClassification', label: 'Clasificacion', type: 'select', options: classificationOptions, nullable: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'charge-types',
    path: 'charge-types',
    title: 'Conceptos de cobro',
    singular: 'concepto',
    eyebrow: 'Configuracion tributaria',
    endpoint: '/api/v1/charge-types',
    icon: CircleDollarSign,
    accent: 'amber',
    operations: { create: true, update: true, delete: true },
    columns: [
      { key: 'code', label: 'Codigo' },
      { key: 'name', label: 'Nombre' },
      { key: 'description', label: 'Descripcion' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'code', label: 'Codigo', required: true },
      { key: 'name', label: 'Nombre', required: true },
      { key: 'description', label: 'Descripcion', nullable: true },
      { key: 'active', label: 'Estado', type: 'select', options: boolOptions, default: true },
    ],
  },
  {
    key: 'liquidations',
    path: 'liquidations',
    title: 'Liquidaciones',
    singular: 'liquidacion',
    eyebrow: 'Cartera',
    endpoint: '/api/v1/liquidations',
    icon: ReceiptText,
    accent: 'rose',
    operations: { create: true, update: false, delete: false },
    relatedActions: [
      { label: 'Ver pagos', method: 'GET', path: '/api/v1/liquidations/{id}/payments' },
      { label: 'Registrar pago', method: 'POST', path: '/api/v1/liquidations/{id}/payments' },
      { label: 'Generar paz y salvo', method: 'POST', path: '/api/v1/liquidations/{id}/clearance-certificates' },
    ],
    columns: [
      { key: 'cadastralCode', label: 'Predio' },
      { key: 'fiscalYear', label: 'Anio' },
      { key: 'totalAmount', label: 'Total', value: (row) => money(row.totalAmount), align: 'right' },
      { key: 'balance', label: 'Saldo', value: (row) => money(row.balance), align: 'right' },
      { key: 'status', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'propertyId', label: 'ID predio', type: 'number', required: true },
      { key: 'fiscalYear', label: 'Anio fiscal', type: 'number', required: true },
      { key: 'discountAmount', label: 'Descuento', type: 'number', default: 0, nullable: true },
      { key: 'interestAmount', label: 'Interes', type: 'number', default: 0, nullable: true },
      { key: 'dueDate', label: 'Fecha vencimiento', type: 'date', nullable: true },
    ],
  },
  {
    key: 'clearance-certificates',
    path: 'clearance-certificates',
    title: 'Paz y salvo',
    singular: 'certificado',
    eyebrow: 'Certificados',
    endpoint: '/api/v1/clearance-certificates',
    icon: FileCheck2,
    accent: 'teal',
    operations: { create: false, update: false, delete: false },
    relatedActions: [{ label: 'Generar desde liquidacion', method: 'POST', path: '/api/v1/liquidations/{id}/clearance-certificates' }],
    columns: [
      { key: 'certificateNumber', label: 'Certificado' },
      { key: 'cadastralCode', label: 'Predio' },
      { key: 'issuedAt', label: 'Emitido', value: (row) => date(row.issuedAt) },
      { key: 'status', label: 'Estado', type: 'status' },
    ],
    fields: [],
  },
]

export const authResource = {
  key: 'auth',
  path: 'auth',
  title: 'Autenticacion',
  eyebrow: 'Seguridad',
  endpoint: '/api/v1/auth',
  icon: ShieldCheck,
  accent: 'slate',
  operations: { create: false, update: false, delete: false },
  relatedActions: [
    { label: 'Iniciar sesion', method: 'POST', path: '/api/v1/auth/login' },
    { label: 'Usuario actual', method: 'GET', path: '/api/v1/auth/me' },
  ],
  fields: [
    { key: 'username', label: 'Usuario', required: true },
    { key: 'password', label: 'Contrasena', type: 'password', required: true },
  ],
}

export const paymentFormFields = [
  { key: 'amount', label: 'Monto', type: 'number', required: true },
  { key: 'paymentMethod', label: 'Metodo de pago', type: 'select', options: paymentMethodOptions, required: true },
  { key: 'reference', label: 'Referencia', nullable: true },
]

export const propertyOwnerFields = [
  { key: 'ownerId', label: 'ID propietario', type: 'number', required: true },
  { key: 'ownershipPercentage', label: 'Porcentaje de propiedad', type: 'number', required: true },
]

export const navGroups = [
  {
    label: 'Operacion',
    items: [
      { label: 'Panel', path: '/', icon: Building2 },
      ...resources.filter((item) => ['municipalities', 'owners', 'properties'].includes(item.key)),
    ],
  },
  {
    label: 'Cartera',
    items: resources.filter((item) => ['liquidations', 'clearance-certificates'].includes(item.key)),
  },
  {
    label: 'Configuracion',
    items: resources.filter((item) => ['fiscal-years', 'tax-rates', 'tax-benefits', 'charge-types'].includes(item.key)),
  },
  {
    label: 'Seguridad',
    items: [authResource],
  },
]

export const dashboardStats = [
  { label: 'Recaudo del mes', value: '$428.6M', detail: 'desde pagos registrados', icon: WalletCards, tone: 'sky' },
  { label: 'Predios activos', value: '18,240', detail: 'catastro municipal', icon: Home, tone: 'cyan' },
  { label: 'Liquidaciones', value: '2,864', detail: 'vigencia actual', icon: ReceiptText, tone: 'blue' },
  { label: 'Paz y salvo', value: '746', detail: 'certificados emitidos', icon: BadgeCheck, tone: 'emerald' },
]
