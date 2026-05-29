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
  Tags,
  UsersRound,
  WalletCards,
} from '@lucide/vue'

export const boolOptions = [
  { label: 'Activo', value: true },
  { label: 'Inactivo', value: false },
]

export const propertyUseOptions = ['RESIDENTIAL', 'COMMERCIAL', 'INDUSTRIAL', 'RURAL', 'INSTITUTIONAL']
export const propertyStatusOptions = ['ACTIVE', 'INACTIVE', 'BLOCKED']
export const classificationOptions = ['URBAN', 'RURAL']
export const paymentMethodOptions = ['CASH', 'TRANSFER', 'DEPOSIT', 'CARD', 'OTHER']

export const money = (value) => value == null ? '-' : new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
}).format(Number(value))

export const date = (value) => value ? new Intl.DateTimeFormat('es-CO').format(new Date(value)) : '-'

export const nested = (path) => (row) => path.split('.').reduce((value, key) => value?.[key], row) ?? '-'

const source = (endpoint, labelKey, valueKey = 'id') => ({ endpoint, labelKey, valueKey })

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
      { key: 'name', label: 'Nombre', required: true, maxlength: 100 },
      { key: 'department', label: 'Departamento', required: true, maxlength: 100 },
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
      { key: 'identificationNumber', label: 'Documento', required: true, maxlength: 30 },
      { key: 'fullName', label: 'Nombre completo', required: true, maxlength: 120 },
      { key: 'phone', label: 'Telefono', maxlength: 30, nullable: true },
      { key: 'email', label: 'Email', type: 'email', maxlength: 120, nullable: true },
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
    columns: [
      { key: 'cadastralCode', label: 'Codigo' },
      { key: 'address', label: 'Direccion' },
      { key: 'municipality.name', label: 'Municipio', value: nested('municipality.name') },
      { key: 'propertyUse', label: 'Uso' },
      { key: 'status', label: 'Estado', type: 'status' },
      { key: 'cadastralValue', label: 'Valor', value: (row) => money(row.cadastralValue), align: 'right' },
    ],
    fields: [
      { key: 'cadastralCode', label: 'Codigo catastral', required: true, maxlength: 40 },
      { key: 'address', label: 'Direccion', required: true, maxlength: 180 },
      { key: 'propertyUse', label: 'Uso', type: 'select', options: propertyUseOptions, required: true },
      { key: 'status', label: 'Estado', type: 'select', options: propertyStatusOptions, default: 'ACTIVE' },
      { key: 'cadastralValue', label: 'Valor catastral', type: 'number', min: 0.01, step: 0.01, required: true },
      {
        key: 'municipalityId',
        label: 'Municipio',
        type: 'select',
        source: source('/api/v1/municipalities', (item) => `${item.name} - ${item.department}`),
        required: true,
        fromRow: (row) => row.municipality?.id ?? '',
      },
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
    columns: [
      { key: 'year', label: 'Anio' },
      { key: 'description', label: 'Descripcion' },
      { key: 'startDate', label: 'Inicio', value: (row) => date(row.startDate) },
      { key: 'endDate', label: 'Fin', value: (row) => date(row.endDate) },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'year', label: 'Anio', type: 'number', required: true },
      { key: 'description', label: 'Descripcion', type: 'textarea', nullable: true },
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
    columns: [
      { key: 'year', label: 'Anio' },
      { key: 'classification', label: 'Clasificacion' },
      { key: 'ratePerThousand', label: 'Tarifa por mil' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      {
        key: 'fiscalYearId',
        label: 'Vigencia fiscal',
        type: 'select',
        source: source('/api/v1/fiscal-years', (item) => `${item.year}${item.active ? ' (activa)' : ''}`),
        required: true,
      },
      { key: 'classification', label: 'Clasificacion', type: 'select', options: classificationOptions, required: true },
      { key: 'ratePerThousand', label: 'Tarifa por mil', type: 'number', min: 0.01, step: 0.01, required: true },
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
    columns: [
      { key: 'code', label: 'Codigo' },
      { key: 'name', label: 'Nombre' },
      { key: 'discountPercentage', label: 'Descuento', value: (row) => `${row.discountPercentage}%` },
      { key: 'applicableClassification', label: 'Clasificacion' },
      { key: 'active', label: 'Estado', type: 'status' },
    ],
    fields: [
      { key: 'code', label: 'Codigo', required: true, maxlength: 20 },
      { key: 'name', label: 'Nombre', required: true, maxlength: 100 },
      { key: 'description', label: 'Descripcion', type: 'textarea', nullable: true },
      { key: 'discountPercentage', label: 'Porcentaje descuento', type: 'number', min: 0, max: 100, step: 0.01, required: true },
      { key: 'applicableClassification', label: 'Clasificacion aplicable', type: 'select', options: classificationOptions, nullable: true },
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
      { key: 'code', label: 'Codigo', required: true, maxlength: 20 },
      { key: 'name', label: 'Nombre', required: true, maxlength: 100 },
      { key: 'description', label: 'Descripcion', type: 'textarea', nullable: true },
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
    filters: [{ key: 'propertyId', label: 'Predio', type: 'select', source: source('/api/v1/properties', (item) => item.cadastralCode) }],
    columns: [
      { key: 'cadastralCode', label: 'Predio' },
      { key: 'fiscalYear', label: 'Anio' },
      { key: 'totalAmount', label: 'Total', value: (row) => money(row.totalAmount), align: 'right' },
      { key: 'balance', label: 'Saldo', value: (row) => money(row.balance), align: 'right' },
      { key: 'status', label: 'Estado', type: 'status' },
    ],
    fields: [
      {
        key: 'propertyId',
        label: 'Predio',
        type: 'select',
        source: source('/api/v1/properties', (item) => `${item.cadastralCode} - ${item.address}`),
        required: true,
      },
      { key: 'fiscalYear', label: 'Anio fiscal', type: 'number', required: true },
      { key: 'discountAmount', label: 'Descuento', type: 'number', min: 0, step: 0.01, default: 0, nullable: true },
      { key: 'interestAmount', label: 'Interes', type: 'number', min: 0, step: 0.01, default: 0, nullable: true },
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
    columns: [
      { key: 'certificateNumber', label: 'Certificado' },
      { key: 'cadastralCode', label: 'Predio' },
      { key: 'issuedAt', label: 'Emitido', value: (row) => date(row.issuedAt) },
      { key: 'status', label: 'Estado', type: 'status' },
    ],
    fields: [],
  },
]

export const paymentFormFields = [
  { key: 'amount', label: 'Monto', type: 'number', min: 0.01, step: 0.01, required: true },
  { key: 'paymentMethod', label: 'Metodo de pago', type: 'select', options: paymentMethodOptions, required: true },
  { key: 'reference', label: 'Referencia', maxlength: 80, nullable: true },
]

export const propertyOwnerFields = [
  {
    key: 'ownerId',
    label: 'Propietario',
    type: 'select',
    source: source('/api/v1/owners', (item) => `${item.identificationNumber} - ${item.fullName}`),
    required: true,
  },
  { key: 'ownershipPercentage', label: 'Porcentaje de propiedad', type: 'number', min: 0.01, max: 100, step: 0.01, required: true },
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
]

export const dashboardFallbackStats = [
  { label: 'Total liquidado', value: '-', detail: 'liquidaciones registradas', icon: ReceiptText, tone: 'sky' },
  { label: 'Total recaudado', value: '-', detail: 'pagos aplicados', icon: WalletCards, tone: 'cyan' },
  { label: 'Cartera pendiente', value: '-', detail: 'saldo por recaudar', icon: Home, tone: 'blue' },
  { label: 'Liquidaciones activas', value: '-', detail: 'por estado', icon: BadgeCheck, tone: 'emerald' },
]
