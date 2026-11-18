import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';

describe('App (unauthenticated)', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('shows the login form for a visitor', async () => {
    render(<App />);

    expect(await screen.findByText('Войти в аккаунт')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Войти' })).toBeInTheDocument();
  });

  test('can switch from login to registration', async () => {
    render(<App />);

    userEvent.click(await screen.findByText('Нет аккаунта? Зарегистрироваться'));

    expect(await screen.findByText('Создать аккаунт')).toBeInTheDocument();
  });
});
