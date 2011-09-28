/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.module;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autentia.wuija.widget.menu.MenuBar;
import com.autentia.wuija.widget.menu.MenuItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class WuijaModuleTest {

	private static final String[] EXPECTED_MENU_ITEMS = { "user1", "user2", "product1" };

	@Resource
	private WuijaApplication moduleManager;

	@Test
	public void shouldRegisterModulesInOrder() {
		final MenuBar menuBar = moduleManager.getMenuBar();
		final int menuItemsCount = menuBar.getItems().size();

		for (int i = 0; i < menuItemsCount; i++) {
			final MenuItem menuItem = menuBar.getItems().get(i);
			Assert.assertEquals("MenuItem label id", EXPECTED_MENU_ITEMS[i], menuItem.getLabelId());
		}
	}
}
