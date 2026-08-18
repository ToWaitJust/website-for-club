import { pgTable, serial, varchar, text, timestamp } from "drizzle-orm/pg-core"
import { sql } from "drizzle-orm"



export const feedback = pgTable("feedback", {
	id: serial().primaryKey().notNull(),
	name: varchar({ length: 100 }).notNull(),
	content: text().notNull(),
	page: varchar({ length: 50 }).default('general'),
	createdAt: timestamp("created_at", { withTimezone: true, mode: 'string' }).defaultNow(),
});

export const healthCheck = pgTable("health_check", {
	id: serial().notNull(),
	updatedAt: timestamp("updated_at", { withTimezone: true, mode: 'string' }).defaultNow(),
});
